CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- USER ACCOUNTS
CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,

                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100) NOT NULL,

                       student_number VARCHAR(50) UNIQUE,

                       account_status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',

                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- MEMBERSHIP APPLICATIONS
CREATE TABLE membership_applications (
                                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                                         user_id UUID NOT NULL REFERENCES users(id),

                                         motivation TEXT,
                                         status VARCHAR(30) NOT NULL DEFAULT 'PENDING',

                                         reviewed_by UUID REFERENCES users(id),
                                         reviewed_at TIMESTAMP,

                                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- MEMBERSHIP
CREATE TABLE memberships (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                             user_id UUID NOT NULL UNIQUE REFERENCES users(id),

                             status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',

                             joined_at TIMESTAMP,
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ROLES
CREATE TABLE roles (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                       name VARCHAR(100) NOT NULL UNIQUE,
                       description TEXT
);

-- PERMISSIONS
CREATE TABLE permissions (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                             code VARCHAR(100) NOT NULL UNIQUE,
                             description TEXT
);

-- MEMBERSHIP ROLE ASSIGNMENTS
CREATE TABLE membership_roles (
                                  membership_id UUID NOT NULL REFERENCES memberships(id),
                                  role_id UUID NOT NULL REFERENCES roles(id),

                                  PRIMARY KEY (membership_id, role_id)
);

-- ROLE PERMISSIONS
CREATE TABLE role_permissions (
                                  role_id UUID NOT NULL REFERENCES roles(id),
                                  permission_id UUID NOT NULL REFERENCES permissions(id),

                                  PRIMARY KEY (role_id, permission_id)
);

-- INDEXES
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_application_status
    ON membership_applications(status);

CREATE INDEX idx_membership_status
    ON memberships(status);