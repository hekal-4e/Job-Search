package com.example.jobsearchapp.model

enum class JobCategory(val displayName: String) {
    TECHNICAL("Technical & IT"),
    SOFTWARE_DEVELOPMENT("Software Development"),
    DATA_SCIENCE("Data Science & Analytics"),
    CYBERSECURITY("Cybersecurity"),
    NETWORK_ADMIN("Network Administration"),
    CLOUD_COMPUTING("Cloud Computing"),

    BUSINESS("Business & Management"),
    FINANCE("Finance & Accounting"),
    MARKETING("Marketing & Advertising"),
    SALES("Sales"),
    HUMAN_RESOURCES("Human Resources"),
    PROJECT_MANAGEMENT("Project Management"),
    CONSULTING("Consulting"),

    HEALTHCARE("Healthcare"),
    MEDICINE("Medicine"),
    NURSING("Nursing"),
    PHARMACY("Pharmacy"),
    MENTAL_HEALTH("Mental Health"),
    HEALTHCARE_ADMIN("Healthcare Administration"),

    EDUCATION("Education & Training"),
    TEACHING("Teaching"),
    RESEARCH("Research & Development"),
    ACADEMIC("Academic"),

    CREATIVE("Creative & Design"),
    MEDIA("Media & Communications"),
    WRITING("Writing & Editing"),
    GRAPHIC_DESIGN("Graphic Design"),
    UX_DESIGN("UX/UI Design"),
    PHOTOGRAPHY("Photography & Videography"),

    ENGINEERING("Engineering"),
    CIVIL_ENGINEERING("Civil Engineering"),
    MECHANICAL_ENGINEERING("Mechanical Engineering"),
    ELECTRICAL_ENGINEERING("Electrical Engineering"),
    CONSTRUCTION("Construction"),
    ARCHITECTURE("Architecture"),

    LEGAL("Legal"),
    LAW("Law"),
    PARALEGAL("Paralegal"),

    HOSPITALITY("Hospitality & Tourism"),
    FOOD_SERVICE("Food Service"),
    RETAIL("Retail"),
    CUSTOMER_SERVICE("Customer Service"),

    TRANSPORTATION("Transportation & Logistics"),
    SUPPLY_CHAIN("Supply Chain"),
    WAREHOUSE("Warehouse & Distribution"),

    MANUFACTURING("Manufacturing & Production"),
    QUALITY_ASSURANCE("Quality Assurance"),

    GOVERNMENT("Government & Public Service"),
    MILITARY("Military"),
    NONPROFIT("Nonprofit & NGO"),
    PUBLIC_POLICY("Public Policy"),

    AGRICULTURE("Agriculture & Farming"),
    ENVIRONMENTAL("Environmental & Sustainability"),

    SCIENCE("Science & Research"),
    PHARMACEUTICAL("Pharmaceutical"),
    BIOTECHNOLOGY("Biotechnology"),

    REAL_ESTATE("Real Estate & Property"),
    PROPERTY_MANAGEMENT("Property Management"),

    ENERGY("Energy & Utilities"),
    OIL_GAS("Oil & Gas"),
    RENEWABLE_ENERGY("Renewable Energy"),

    OTHER("Other")
}
