INSERT INTO permissions (code, description) VALUES
                                                ('MEMBER_VIEW', 'View club members'),
                                                ('MEMBER_APPROVE', 'Approve membership applications'),
                                                ('MEMBER_REJECT', 'Reject membership applications'),
                                                ('MEMBER_SUSPEND', 'Suspend or reactivate memberships'),
                                                ('MEMBER_ROLE_ASSIGN', 'Assign and remove member roles'),

                                                ('ROLE_VIEW', 'View roles and permissions'),
                                                ('ROLE_MANAGE', 'Create, update and delete roles'),

                                                ('EVENT_VIEW', 'View club events'),
                                                ('EVENT_CREATE', 'Create events'),
                                                ('EVENT_UPDATE', 'Update events'),
                                                ('EVENT_DELETE', 'Delete events'),
                                                ('EVENT_REGISTER', 'Register for events'),
                                                ('EVENT_APPROVE_REGISTRATION', 'Approve event registrations'),
                                                ('EVENT_MANAGE_ATTENDANCE', 'Manage event attendance'),

                                                ('INVENTORY_VIEW', 'View club inventory'),
                                                ('INVENTORY_CREATE', 'Add inventory items'),
                                                ('INVENTORY_UPDATE', 'Update inventory items'),
                                                ('INVENTORY_DELETE', 'Delete inventory items'),
                                                ('INVENTORY_BORROW_APPROVE', 'Approve borrowing requests'),
                                                ('INVENTORY_RETURN', 'Manage item returns'),

                                                ('CHAT_CREATE', 'Create conversations'),
                                                ('CHAT_MANAGE', 'Manage conversations'),

                                                ('DOCUMENT_VIEW', 'View club documents'),
                                                ('DOCUMENT_MANAGE', 'Upload and manage documents'),

                                                ('AUDIT_VIEW', 'View audit logs'),
                                                ('DASHBOARD_VIEW', 'View the club dashboard'),
                                                ('CLUB_SETTINGS_MANAGE', 'Manage club settings')
    ON CONFLICT (code) DO NOTHING;


INSERT INTO roles (name, description) VALUES
                                          ('PRESIDENT', 'Full administrative access to the club'),
                                          ('VICE_PRESIDENT', 'Assists the president in club management'),
                                          ('SECRETARY', 'Manages administrative activities and records'),
                                          ('TREASURER', 'Manages financial activities'),
                                          ('EVENT_MANAGER', 'Manages club events'),
                                          ('INVENTORY_MANAGER', 'Manages club equipment and inventory'),
                                          ('COMMUNICATION_MANAGER', 'Manages club communications'),
                                          ('MEMBER', 'Standard club member')
    ON CONFLICT (name) DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         CROSS JOIN permissions p
WHERE r.name = 'PRESIDENT'
    ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         JOIN permissions p ON p.code IN (
                                          'MEMBER_VIEW',
                                          'MEMBER_APPROVE',
                                          'MEMBER_REJECT',
                                          'MEMBER_SUSPEND',
                                          'MEMBER_ROLE_ASSIGN',
                                          'ROLE_VIEW',
                                          'EVENT_VIEW',
                                          'EVENT_CREATE',
                                          'EVENT_UPDATE',
                                          'EVENT_DELETE',
                                          'EVENT_REGISTER',
                                          'EVENT_APPROVE_REGISTRATION',
                                          'EVENT_MANAGE_ATTENDANCE',
                                          'INVENTORY_VIEW',
                                          'INVENTORY_CREATE',
                                          'INVENTORY_UPDATE',
                                          'INVENTORY_DELETE',
                                          'INVENTORY_BORROW_APPROVE',
                                          'INVENTORY_RETURN',
                                          'CHAT_CREATE',
                                          'CHAT_MANAGE',
                                          'DOCUMENT_VIEW',
                                          'DOCUMENT_MANAGE',
                                          'AUDIT_VIEW',
                                          'DASHBOARD_VIEW'
    )
WHERE r.name = 'VICE_PRESIDENT'
    ON CONFLICT DO NOTHING;

-- Secretary
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         JOIN permissions p ON p.code IN (
                                          'MEMBER_VIEW',
                                          'EVENT_VIEW',
                                          'DOCUMENT_VIEW',
                                          'DOCUMENT_MANAGE',
                                          'DASHBOARD_VIEW',
                                          'AUDIT_VIEW'
    )
WHERE r.name = 'SECRETARY'
    ON CONFLICT DO NOTHING;

-- Treasurer
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         JOIN permissions p ON p.code IN (
                                          'DASHBOARD_VIEW',
                                          'DOCUMENT_VIEW'
    )
WHERE r.name = 'TREASURER'
    ON CONFLICT DO NOTHING;

-- Event manager
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         JOIN permissions p ON p.code IN (
                                          'EVENT_VIEW',
                                          'EVENT_CREATE',
                                          'EVENT_UPDATE',
                                          'EVENT_DELETE',
                                          'EVENT_REGISTER',
                                          'EVENT_APPROVE_REGISTRATION',
                                          'EVENT_MANAGE_ATTENDANCE',
                                          'DOCUMENT_VIEW',
                                          'DASHBOARD_VIEW'
    )
WHERE r.name = 'EVENT_MANAGER'
    ON CONFLICT DO NOTHING;

-- Inventory manager
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         JOIN permissions p ON p.code IN (
                                          'INVENTORY_VIEW',
                                          'INVENTORY_CREATE',
                                          'INVENTORY_UPDATE',
                                          'INVENTORY_DELETE',
                                          'INVENTORY_BORROW_APPROVE',
                                          'INVENTORY_RETURN',
                                          'DASHBOARD_VIEW'
    )
WHERE r.name = 'INVENTORY_MANAGER'
    ON CONFLICT DO NOTHING;

-- Communication manager
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         JOIN permissions p ON p.code IN (
                                          'CHAT_CREATE',
                                          'CHAT_MANAGE',
                                          'DOCUMENT_VIEW',
                                          'DASHBOARD_VIEW'
    )
WHERE r.name = 'COMMUNICATION_MANAGER'
    ON CONFLICT DO NOTHING;

-- Standard member
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         JOIN permissions p ON p.code IN (
                                          'EVENT_VIEW',
                                          'EVENT_REGISTER',
                                          'DOCUMENT_VIEW',
                                          'CHAT_CREATE'
    )
WHERE r.name = 'MEMBER'
    ON CONFLICT DO NOTHING;

CREATE UNIQUE INDEX IF NOT EXISTS uq_pending_application_per_user
    ON membership_applications(user_id)
    WHERE status = 'PENDING';