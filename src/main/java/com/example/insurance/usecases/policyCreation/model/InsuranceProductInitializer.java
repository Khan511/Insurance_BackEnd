// package com.example.insurance.usecases.policyCreation.model;

// import java.math.BigDecimal;
// import java.time.LocalDate;
// import java.util.Arrays;
// import java.util.HashMap;
// import java.util.HashSet;
// import java.util.List;
// // import java.util.Locale;
// import java.util.Map;
// import java.util.Set;
// import org.springframework.stereotype.Component;
// import com.example.insurance.common.enummuration.ClaimDocumentType;
// import com.example.insurance.common.enummuration.ProductType;
// import
// com.example.insurance.domain.insuranceCategory.model.InsuranceCategory;
// import
// com.example.insurance.domain.insuranceCategory.repository.InsuranceCategoryRepository;
// import com.example.insurance.domain.insuranceProduct.model.InsuranceProduct;
// import
// com.example.insurance.domain.insuranceProduct.model.PremiumCalculationConfig;
// import
// com.example.insurance.domain.insuranceProduct.repository.InsuranceProductRepository;
// import com.example.insurance.embeddable.CoverageDetail;
// import com.example.insurance.embeddable.ProductTranslation;
// import com.example.insurance.shared.kernel.embeddables.MonetaryAmount;
// import com.example.insurance.shared.kernel.embeddables.PolicyPeriod;
// import jakarta.annotation.PostConstruct;
// import jakarta.transaction.Transactional;
// import lombok.RequiredArgsConstructor;

// @Component
// @RequiredArgsConstructor
// public class InsuranceProductInitializer {

// private final InsuranceProductRepository productRepository;
// private final InsuranceCategoryRepository categoryRepository;

// @PostConstruct
// @Transactional
// public void init() {
// if (productRepository.count() == 0) {
// createCategories();
// createProducts();
// }
// }

// private void createCategories() {
// List<InsuranceCategory> categories = Arrays.asList(
// new InsuranceCategory("Auto", "Vehicle-related insurance products"),
// new InsuranceCategory("Property", "Home and property insurance"),
// new InsuranceCategory("Life", "Life insurance products"));
// categoryRepository.saveAll(categories);
// }

// private void createProducts() {
// // Get categories
// InsuranceCategory autoCategory = categoryRepository.findByName("Auto")
// .orElseThrow(() -> new IllegalStateException("Auto category not found"));
// InsuranceCategory propertyCategory =
// categoryRepository.findByName("Property")
// .orElseThrow(() -> new IllegalStateException("Property category not found"));
// InsuranceCategory lifeCategory = categoryRepository.findByName("Life")
// .orElseThrow(() -> new IllegalStateException("Life category not found"));

// // Create products
// List<InsuranceProduct> products = Arrays.asList(
// createAutoInsurance(autoCategory),
// createHomeInsurance(propertyCategory),
// createLifeInsurance(lifeCategory));

// productRepository.saveAll(products);
// }

// private InsuranceProduct createAutoInsurance(InsuranceCategory category) {
// InsuranceProduct product = new InsuranceProduct();
// product.setProductCode("AUTO-2025");
// product.setDisplayName("Comprehensive Auto Insurance");
// product.setDescription("Full coverage for your vehicle including collision,
// theft, and third-party liability");
// product.setProductType(ProductType.AUTO);

// // Pricing
// product.setBasePremium(new MonetaryAmount(new BigDecimal("850.00"), "DKK"));

// // Coverage details
// Set<CoverageDetail> coverages = new HashSet<>();
// coverages.add(new CoverageDetail(
// "Collision",
// "Damage from vehicle collisions",
// new MonetaryAmount(new BigDecimal("50000.00"), "DKK"),
// new MonetaryAmount(new BigDecimal("500.00"), "DKK")));
// coverages.add(new CoverageDetail(
// "Theft",
// "Vehicle theft protection",
// new MonetaryAmount(new BigDecimal("40000.00"), "DKK"),
// new MonetaryAmount(new BigDecimal("1000.00"), "DKK")));
// product.setCoverageDetails(coverages);

// // Eligibility rules
// Map<String, String> eligibility = new HashMap<>();
// eligibility.put("minAge", "18");
// eligibility.put("licenseType", "VALID_DRIVERS_LICENSE");
// eligibility.put("vehicleAge", "<10");
// product.setEligibilityRules(eligibility);

// // Premium calculation
// PremiumCalculationConfig calcConfig = new PremiumCalculationConfig();
// calcConfig.setFormula("base + (ageFactor * driverAge) + (valueFactor *
// vehicleValue)");
// calcConfig.setFactors(Map.of(
// "ageFactor", new BigDecimal("25.0"),
// "valueFactor", new BigDecimal("0.02")));
// // Add age brackets if needed
// List<PremiumCalculationConfig.AgeBracket> ageBrackets = List.of(
// new PremiumCalculationConfig.AgeBracket(18, 25, new BigDecimal("1.5"), null),
// new PremiumCalculationConfig.AgeBracket(26, 40, new BigDecimal("1.2"), null),
// new PremiumCalculationConfig.AgeBracket(41, 65, new BigDecimal("1.0"),
// null));

// calcConfig.setAgeBrackets(ageBrackets);
// calcConfig.setIncludesTax(true);
// calcConfig.setCommissionRate(new BigDecimal("0.10"));
// product.setCalculationConfig(calcConfig);

// // Sales info
// product.setTargetAudience(Arrays.asList("Car owners", "Ride-share drivers"));
// product.setRegion(Arrays.asList("Denmark", "Swedan", "Norway"));
// product.setCategory(category);

// // Operational
// product.setArchived(false);
// product.setValidityPeriod(new PolicyPeriod(
// LocalDate.now(),
// LocalDate.now().plusYears(1)));
// product.setAllowedClaimTypes(Set.of(
// ClaimDocumentType.RequiredDocument.INCIDENT_REPORT,
// ClaimDocumentType.RequiredDocument.POLICE_REPORT,
// ClaimDocumentType.RequiredDocument.REPAIR_ESTIMATE));

// // Translations
// Map<String, ProductTranslation> translations = new HashMap<>();
// translations.put("da_DK", new ProductTranslation( // Danish (Denmark)
// "Omfattende Bilforsikring",
// "Fuld dækning for din bil inklusive kollision, tyveri og tredjemand
// ansvar"));
// translations.put("sv_SE", new ProductTranslation( // Swedish (Sweden)
// "Omfattande Bilförsäkring",
// "Full täckning för ditt fordon inklusive kollision, stöld och
// tredjepartsansvar"));
// translations.put("nb_NO", new ProductTranslation( // Norwegian Bokmål(Norway)
// "Omfattende Bilforsikring",
// "Full dekning for bilen din inkludert kollisjon, tyveri og
// tredjepartsansvar"));
// product.setTranslation(translations);

// return product;
// }

// private InsuranceProduct createHomeInsurance(InsuranceCategory category) {
// InsuranceProduct product = new InsuranceProduct();
// product.setProductCode("HOME-2025");
// product.setDisplayName("Premium Homeowners Insurance");
// product.setDescription("Complete protection for your home against natural
// disasters, theft, and liability");
// product.setProductType(ProductType.PROPERTY);

// product.setBasePremium(new MonetaryAmount(new BigDecimal("1200.00"), "DKK"));

// Set<CoverageDetail> coverages = new HashSet<>();
// coverages.add(new CoverageDetail(
// "Dwelling",
// "Main structure coverage",
// new MonetaryAmount(new BigDecimal("750000.00"), "DKK"),
// new MonetaryAmount(new BigDecimal("1000.00"), "DKK")));
// coverages.add(new CoverageDetail(
// "Personal Property",
// "Belongings protection",
// new MonetaryAmount(new BigDecimal("150000.00"), "DKK"),
// new MonetaryAmount(new BigDecimal("500.00"), "DKK")));
// product.setCoverageDetails(coverages);

// Map<String, String> eligibility = new HashMap<>();
// eligibility.put("propertyType", "SINGLE_FAMILY|CONDO");
// eligibility.put("constructionYear", ">1980");
// product.setEligibilityRules(eligibility);

// PremiumCalculationConfig calcConfig = new PremiumCalculationConfig();

// calcConfig.setFormula("base + (locationFactor * riskZone) + (valueFactor *
// homeValue)");
// calcConfig.addFactor("locationFactor", new BigDecimal("150.0"));
// calcConfig.addFactor("valueFactor", new BigDecimal("0.0015"));
// product.setCalculationConfig(calcConfig);

// product.setTargetAudience(Arrays.asList("Homeowners", "Property investors"));
// product.setRegion(Arrays.asList("Denmark", "Swedan", "Norway"));
// product.setCategory(category);
// product.setArchived(false);
// product.setValidityPeriod(new PolicyPeriod(
// LocalDate.now(),
// LocalDate.now().plusYears(1)));
// product.setAllowedClaimTypes(Set.of(
// ClaimDocumentType.RequiredDocument.PROPERTY_DAMAGE_REPORT,
// ClaimDocumentType.RequiredDocument.ESTIMATE,
// ClaimDocumentType.RequiredDocument.INVENTORY_LIST));

// // Add Nordic translations
// Map<String, ProductTranslation> translations = new HashMap<>();
// translations.put("da_DK", new ProductTranslation( // Danish (Denmark)
// "Premium Husejerforsikring",
// "Komplet beskyttelse for dit hjem mod naturkatastrofer, tyveri og ansvar"));
// translations.put("sv_SE", new ProductTranslation( // Swedish (Sweden)
// "Premium Hemförsäkring",
// "Komplett skydd för ditt hem mot naturkatastrofer, stöld och ansvar"));
// translations.put("nb_NO", new ProductTranslation( // Norwegian Bokmål(Norway)
// "Premium Huseierforsikring",
// "Full beskyttelse for hjemmet ditt mot naturkatastrofer, tyveri og ansvar"));
// product.setTranslation(translations);

// return product;
// }

// private InsuranceProduct createLifeInsurance(InsuranceCategory category) {
// InsuranceProduct product = new InsuranceProduct();
// product.setProductCode("LIFE-2025");
// product.setDisplayName("Term Life Insurance");
// product.setDescription("Financial protection for your family with flexible
// term options");
// product.setProductType(ProductType.LIFE);

// product.setBasePremium(new MonetaryAmount(new BigDecimal("300.00"), "DKK"));

// Set<CoverageDetail> coverages = new HashSet<>();
// coverages.add(new CoverageDetail(
// "Death Benefit",
// "Primary coverage amount",
// new MonetaryAmount(new BigDecimal("500000.00"), "DKK"),
// null));
// coverages.add(new CoverageDetail(
// "Accidental Death",
// "Additional accidental coverage",
// new MonetaryAmount(new BigDecimal("250000.00"), "DKK"),
// null));
// product.setCoverageDetails(coverages);

// Map<String, String> eligibility = new HashMap<>();
// eligibility.put("minAge", "18");
// eligibility.put("maxAge", "65");
// eligibility.put("healthCheck", "REQUIRED");
// product.setEligibilityRules(eligibility);

// PremiumCalculationConfig calcConfig = new PremiumCalculationConfig();

// calcConfig.setFormula("base + (ageFactor * insuredAge) + (healthFactor *
// riskLevel)");
// calcConfig.addFactor("ageFactor", new BigDecimal("15.0"));
// calcConfig.addFactor("healthFactor", new BigDecimal("200.0"));
// product.setCalculationConfig(calcConfig);

// product.setTargetAudience(Arrays.asList("Families", "Breadwinners"));
// product.setRegion(Arrays.asList("Denmark", "Swedan", "Norway"));
// product.setCategory(category);
// product.setArchived(false);
// product.setValidityPeriod(new PolicyPeriod(
// LocalDate.now(),
// LocalDate.now().plusYears(1)));
// product.setAllowedClaimTypes(Set.of(
// ClaimDocumentType.RequiredDocument.DEATH_CERTIFICATE,
// ClaimDocumentType.RequiredDocument.BENEFICIARY_DOCS));

// // Add Nordic translations
// Map<String, ProductTranslation> translations = new HashMap<>();
// translations.put("da_DK", new ProductTranslation( // Danish (Denmark)
// "Tidsbegrænset Livsforsikring",
// "Finansiel beskyttelse til din familie med fleksible løbetidsmuligheder"));
// translations.put("sv_SE", new ProductTranslation( // Swedish (Sweden)
// "Tidsbegränsad Livförsäkring",
// "Finansiellt skydd för din familj med flexibla försäkringsperioder"));
// translations.put("nb_NO", new ProductTranslation( // Norwegian Bokmål(Norway)
// "Tidsbegrenset Livsforsikring",
// "Finansielt beskyttelse for familien din med fleksible
// løpetidsalternativer"));
// product.setTranslation(translations);

// return product;
// }
// }
