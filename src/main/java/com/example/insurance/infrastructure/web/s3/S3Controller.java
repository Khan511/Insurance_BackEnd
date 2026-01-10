package com.example.insurance.infrastructure.web.s3;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.insurance.global.config.CustomUserDetails;
import com.google.common.net.HttpHeaders;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@RestController
@RequestMapping("/api/s3")
// @CrossOrigin(origins = { "http://localhost:3000", "http://localhost:5173" })
public class S3Controller {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucketName;

    public S3Controller(S3Client s3Client, S3Presigner s3Presigner, @Value("${aws.bucket}") String bucketName) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.bucketName = bucketName;
    }

    @PostMapping("/presigned-url")
    public ResponseEntity<?> generatePresignedUrl(@RequestBody PresignedUrlRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {

        try {
            // Validate the file name
            if (request.getFileName() == null || request.getFileName().contains("..")) {
                return ResponseEntity.badRequest().body("Invalid filename");
            }
            // String folder = switch (request.getFolderType()) {
            // case "blog-image" -> "blog-images/";
            // case "blog-documents" -> "blog-documents/";
            // default -> "profile-images/";
            // };

            // Extract file extension
            String safeFilename = request.getFileName().replaceAll("[^a-zA-Z0-9._-]", "_");
            // String extension = safeFilename.contains(".") ?
            // safeFilename.substring(safeFilename.lastIndexOf('.')) : "";

            // User-specific folder
            String userFolder = user.getUserEntity().getName().getFirstName() + "_"
                    + user.getUserEntity().getName().getLastName() + "_" + user.getUserEntity().getUserId();

            // Final key -> userFolder/uuid_originalFilename.ext
            String objectKey = userFolder + "/" + UUID.randomUUID() + "_" + safeFilename;

            // String objectKey = request.getFileName() + "_" + UUID.randomUUID();
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(request.getFileType())
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .putObjectRequest(putObjectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

            // fileUrl = new URI("https", "dwy0mahvkrvvq.cloudfront.net", "/" + objectKey,
            // null).toString();
            String fileUrl = "https://" + bucketName + ".s3.eu-central-1.amazonaws.com/"
                    + objectKey;

            return ResponseEntity.ok(Map.of("presignedUrl", presignedRequest.url().toString(), "fileUrl", fileUrl,
                    "fileKey", objectKey));

        } catch (Exception e) {
            System.err.println("Error generating presigned URL: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error generating presigned URL: " + e.getMessage()));
        }
    }

    @DeleteMapping("/delete-object")
    public ResponseEntity<?> deleteObject(@RequestParam String imageUrl,
            @AuthenticationPrincipal CustomUserDetails user) {

        try {

            String objectKeye = extractKeyFromUrl(imageUrl);
            System.out.println("Attempting to delete object with key: " + objectKeye);

            // Add more detailed logging
            System.out.println("Bucket name: " + bucketName);
            System.out.println("Full S3 path: s3://" + bucketName + "/" + objectKeye);

            String objectKey = extractKeyFromUrl(imageUrl);

            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build());

            return ResponseEntity.ok().build();
        } catch (S3Exception e) {
            System.err.println("S3 Error: " + e.awsErrorDetails().errorMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete object: " + e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete object: " + e.getMessage());
        }
    }

    @GetMapping("/presigned-download-url")
    public ResponseEntity<?> getPresignedDownloadUrl(@RequestParam String fileKey) {
        try {
            // URL decode the fileKey first
            String decodedFileKey = URLDecoder.decode(fileKey, StandardCharsets.UTF_8);

            // Generate a pre-signed URL for download
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(decodedFileKey) // Use decoded key
                    .build();

            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(5))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
            String downloadUrl = presignedGetObjectRequest.url().toString();

            return ResponseEntity.ok(Map.of(
                    "downloadUrl", downloadUrl, // Fixed typo: was "downlaodUrl"
                    "expiresAt", System.currentTimeMillis() + 300000));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to generate download URL: " + e.getMessage()));
        }
    }

    private String extractKeyFromUrl(String imageUrl) {
        try {
            // Handle URL-encded chracters
            // Parse the URI first to get the path
            URI uri = new URI(imageUrl);
            String path = uri.getPath();

            // Remove leading slash if present
            String key = path.startsWith("/") ? path.substring(1) : path;

            // Decode URL-encoded characters(like %20 -> space)
            return URLDecoder.decode(key, StandardCharsets.UTF_8);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid image URL format");
        }
    }

    @GetMapping("/download-file")
    public ResponseEntity<?> downloadFile(@RequestParam String fileKey) {
        try {
            // URL decode the fileKey first
            String decodedFileKey = URLDecoder.decode(fileKey, StandardCharsets.UTF_8);

            // Get the file from S3
            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(
                    GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(decodedFileKey) // Use decoded key
                            .build());

            byte[] data = objectBytes.asByteArray();
            ByteArrayResource resource = new ByteArrayResource(data);

            // Determine content type based on file extension
            String contentType = determineContentType(decodedFileKey);

            // Extract filename from the key
            String filename = getFileName(decodedFileKey);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(data.length)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    private String determineContentType(String fileKey) {
        if (fileKey.endsWith(".pdf"))
            return "application/pdf";
        if (fileKey.endsWith(".jpg") || fileKey.endsWith(".jpeg"))
            return "image/jpeg";
        if (fileKey.endsWith(".png"))
            return "image/png";
        if (fileKey.endsWith(".doc"))
            return "application/msword";
        if (fileKey.endsWith(".docx"))
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        return "application/octet-stream";
    }

    private String getFileName(String fileKey) {
        // Extract the original filename from the fileKey
        String[] parts = fileKey.split("/");
        if (parts.length > 1) {
            String uuidPart = parts[parts.length - 1];
            // Remove the UUID part to get the original filename
            int underscoreIndex = uuidPart.indexOf('_');
            if (underscoreIndex != -1 && underscoreIndex + 1 < uuidPart.length()) {
                return uuidPart.substring(underscoreIndex + 1);
            }
        }
        return fileKey;
    }

}
