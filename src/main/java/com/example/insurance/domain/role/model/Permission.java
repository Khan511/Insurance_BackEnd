package com.example.insurance.domain.role.model;

public enum Permission {
    // General User Management
    CREATE_USER, VIEW_USER, EDIT_USER, DELETE_USER, ASSIGN_ROLE,

    // Policy Management
    CREATE_POLICY, EDIT_POLICY, VIEW_POLICY, DELETE_POLICY, APPROVE_POLICY,

    // Premium & Payment
    CALCULATE_PREMIUM, MAKE_PAYMENT, VIEW_PAYMENT_HISTORY, REFUND_PAYMENT,

    // Claim Management
    FILE_CLAIM, VIEW_CLAIM, APPROVE_CLAIM, REJECT_CLAIM, EDIT_CLAIM,

    // Audit & Logs
    VIEW_AUDIT_LOGS, EXPORT_LOGS,

    // System Settings
    ACCESS_ADMIN_PANEL, MANAGE_SYSTEM_SETTINGS, VIEW_DASHBOARD
}
