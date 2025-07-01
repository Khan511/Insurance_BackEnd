// package com.example.insurance.usecases.policyCreation.policydataseed;

// import java.util.List;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component;

// import
// com.example.insurance.usecases.policyCreation.model.CreateInsuranceCategory;
// import
// com.example.insurance.usecases.policyCreation.model.CreateInsurancePolicy;
// import
// com.example.insurance.usecases.policyCreation.repositories.CreateInsuraceCategoryRepo;
// import
// com.example.insurance.usecases.policyCreation.repositories.CreateInsurancePolicyRepo;

// @Component
// public class PolicyDataSeeder implements CommandLineRunner {

// private final CreateInsuraceCategoryRepo categoryRepository;
// private final CreateInsurancePolicyRepo policyRepository;

// public PolicyDataSeeder(CreateInsuraceCategoryRepo categoryRepository,
// CreateInsurancePolicyRepo policyRepository) {
// this.categoryRepository = categoryRepository;
// this.policyRepository = policyRepository;
// }

// @Override
// public void run(String... args) throws Exception {
// if (categoryRepository.count() == 0) {
// CreateInsuranceCategory privateCategory = new CreateInsuranceCategory();
// privateCategory.setName("Private");

// CreateInsuranceCategory commercialCategory = new CreateInsuranceCategory();
// commercialCategory.setName("Commercial");

// categoryRepository.saveAll(List.of(privateCategory, commercialCategory));

// // Private Policies
// policyRepository.saveAll(List.of(
// new CreateInsurancePolicy("Health Insurance",
// "Provides medical coverage for individuals and families, including doctor
// visits, hospital stays, and surgeries.",
// List.of("Individuals", "Families"),
// List.of("Norway", "Denmark", "Sweden"),
// privateCategory),
// new CreateInsurancePolicy("Auto Insurance",
// "Covers damages or loss of personal vehicles due to accidents, theft, or
// natural disasters.",
// List.of("Individuals"), List.of("Norway", "Denmark", "Sweden"),
// privateCategory),
// new CreateInsurancePolicy("Home Insurance",
// "Protects homes and belongings from risks such as fire, theft, water damage,
// and natural events.",
// List.of("Individuals", "Families"),
// List.of("Norway", "Denmark", "Sweden"),
// privateCategory),
// new CreateInsurancePolicy("Travel Insurance",
// "Provides coverage for medical emergencies, trip cancellations, and lost
// luggage while traveling.",
// List.of("Individuals", "Families"),
// List.of("Norway", "Denmark", "Sweden"),
// privateCategory),
// new CreateInsurancePolicy("Life Insurance",
// "Ensures financial security for dependents in the event of the policyholder's
// death.",
// List.of("Individuals"), List.of("Norway", "Denmark", "Sweden"),
// privateCategory),
// new CreateInsurancePolicy("Pet Insurance",
// "Covers veterinary costs, medications, and emergency treatments for pets.",
// List.of("Individuals", "Families"),
// List.of("Norway", "Denmark", "Sweden"),
// privateCategory),
// new CreateInsurancePolicy("Gadget Insurance",
// "Protects personal electronics like smartphones and laptops against loss,
// theft, or damage.",
// List.of("Individuals", "Students"),
// List.of("Norway", "Denmark", "Sweden"),
// privateCategory),
// new CreateInsurancePolicy("Cyber Insurance",
// "Covers identity theft, online fraud, and personal data breaches.",
// List.of("Individuals", "Remote Workers"),
// List.of("Norway", "Denmark", "Sweden"),
// privateCategory),
// new CreateInsurancePolicy("Event Insurance",
// "Covers financial losses due to event cancellations, weather disruptions, or
// property damage.",
// List.of("Individuals", "Families"),
// List.of("Norway", "Denmark", "Sweden"),
// privateCategory),
// new CreateInsurancePolicy("Climate Risk Insurance",
// "Provides coverage for damage caused by extreme weather events like floods or
// storms.",
// List.of("Homeowners"), List.of("Norway", "Denmark", "Sweden"),
// privateCategory)));

// // Commercial Policies
// policyRepository.saveAll(List.of(
// new CreateInsurancePolicy("Commercial Property Insurance",
// "Covers buildings, equipment, and inventory owned by a business against fire,
// theft, and damage.",
// List.of("Small Businesses", "Enterprises"),
// List.of("Norway", "Denmark", "Sweden"),
// commercialCategory),
// new CreateInsurancePolicy("Public Liability Insurance",
// "Protects businesses from claims of injury or damage caused to third
// parties.",
// List.of("Shops", "Offices", "Service Providers"),
// List.of("Norway", "Denmark", "Sweden"),
// commercialCategory),
// new CreateInsurancePolicy("Professional Indemnity Insurance",
// "Covers legal expenses and compensation claims due to professional mistakes
// or negligence.",
// List.of("Consultants", "Freelancers", "Agencies"),
// List.of("Norway", "Denmark", "Sweden"),
// commercialCategory),
// new CreateInsurancePolicy("Fleet Insurance",
// "Insures multiple vehicles owned by a business under a single policy.",
// List.of("Transport Companies", "Logistics Firms"),
// List.of("Norway", "Denmark", "Sweden"),
// commercialCategory),
// new CreateInsurancePolicy("Employee Health Plans",
// "Group medical insurance coverage for employees of a business or
// organization.",
// List.of("Companies", "Organizations"),
// List.of("Norway", "Denmark", "Sweden"),
// commercialCategory),
// new CreateInsurancePolicy("Cyber Liability Insurance",
// "Protects businesses from financial loss due to cyberattacks, data breaches,
// or digital fraud.",
// List.of("IT Companies", "E-commerce", "Startups"),
// List.of("Norway", "Denmark", "Sweden"),
// commercialCategory),
// new CreateInsurancePolicy("Event Organizer Insurance",
// "Covers liability and cancellation risks for organizers of corporate events
// or conferences.",
// List.of("Event Agencies", "Businesses"),
// List.of("Norway", "Denmark", "Sweden"),
// commercialCategory)));
// }
// }
// }
