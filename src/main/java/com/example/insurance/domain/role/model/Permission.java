
package com.example.insurance.domain.role.model;

public enum Permission {
    // User Management
    VIEW_USER,

    // Policy Management
    CREATE_POLICY,
    EDIT_POLICY,
    VIEW_POLICY,

    // Claim Management
    FILE_CLAIM,
    VIEW_CLAIM,
    EDIT_CLAIM,
    APPROVE_CLAIM,
    REJECT_CLAIM,

    // Payment Management
    MAKE_PAYMENT,
    VIEW_PAYMENT_HISTORY,

    // Premium Calculation
    CALCULATE_PREMIUM,

    // Audit & Logs
    VIEW_AUDIT_LOGS,

    // System Management (Admin only)
    CREATE_USER,
    DELETE_USER,
    EDIT_USER,
    DELETE_POLICY,
    ASSIGN_ROLE,
    REFUND_PAYMENT,
    EXPORT_LOGS,
    ACCESS_ADMIN_PANEL,
    MANAGE_SYSTEM_SETTINGS,
    VIEW_DASHBOARD
}