CREATE TABLE asset_categories (
                                  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                  name VARCHAR(100) NOT NULL UNIQUE,
                                  description TEXT,
                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE assets (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        name VARCHAR(200) NOT NULL,
                        description TEXT,
                        serial_number VARCHAR(150) UNIQUE,
                        inventory_code VARCHAR(100) NOT NULL UNIQUE,
                        category_id UUID NOT NULL REFERENCES asset_categories(id),

                        condition VARCHAR(30) NOT NULL DEFAULT 'GOOD',
                        status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',

                        purchase_date DATE,
                        purchase_price NUMERIC(12,2),
                        location VARCHAR(200),

                        created_by UUID NOT NULL REFERENCES users(id),
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                        CONSTRAINT chk_asset_condition
                            CHECK (condition IN ('NEW','GOOD','FAIR','DAMAGED','BROKEN')),

                        CONSTRAINT chk_asset_status
                            CHECK (status IN (
                                              'AVAILABLE',
                                              'BORROWED',
                                              'MAINTENANCE',
                                              'RETIRED'
                                )),

                        CONSTRAINT chk_asset_purchase_price
                            CHECK (purchase_price IS NULL OR purchase_price >= 0)
);

CREATE TABLE borrow_requests (
                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                                 asset_id UUID NOT NULL REFERENCES assets(id),
                                 user_id UUID NOT NULL REFERENCES users(id),

                                 purpose TEXT,
                                 requested_from TIMESTAMP NOT NULL,
                                 requested_until TIMESTAMP NOT NULL,

                                 status VARCHAR(30) NOT NULL DEFAULT 'PENDING',

                                 reviewed_by UUID REFERENCES users(id),
                                 reviewed_at TIMESTAMP,

                                 borrowed_at TIMESTAMP,
                                 returned_at TIMESTAMP,

                                 notes TEXT,

                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                 CONSTRAINT chk_borrow_dates
                                     CHECK (requested_until > requested_from),

                                 CONSTRAINT chk_borrow_status
                                     CHECK (status IN (
                                                       'PENDING',
                                                       'APPROVED',
                                                       'REJECTED',
                                                       'CANCELLED',
                                                       'RETURNED',
                                                       'OVERDUE'
                                         ))
);

CREATE TABLE asset_maintenance (
                                   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                                   asset_id UUID NOT NULL REFERENCES assets(id),

                                   title VARCHAR(200) NOT NULL,
                                   description TEXT,

                                   maintenance_type VARCHAR(30) NOT NULL,
                                   cost NUMERIC(12,2),

                                   started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   completed_at TIMESTAMP,

                                   performed_by UUID REFERENCES users(id),
                                   recorded_by UUID NOT NULL REFERENCES users(id),

                                   status VARCHAR(30) NOT NULL DEFAULT 'IN_PROGRESS',

                                   CONSTRAINT chk_maintenance_type
                                       CHECK (maintenance_type IN (
                                                                   'REPAIR',
                                                                   'INSPECTION',
                                                                   'CLEANING',
                                                                   'UPGRADE',
                                                                   'OTHER'
                                           )),

                                   CONSTRAINT chk_maintenance_status
                                       CHECK (status IN ('IN_PROGRESS','COMPLETED','CANCELLED')),

                                   CONSTRAINT chk_maintenance_cost
                                       CHECK (cost IS NULL OR cost >= 0)
);

CREATE INDEX idx_assets_category ON assets(category_id);
CREATE INDEX idx_assets_status ON assets(status);
CREATE INDEX idx_assets_inventory_code ON assets(inventory_code);

CREATE INDEX idx_borrow_asset ON borrow_requests(asset_id);
CREATE INDEX idx_borrow_user ON borrow_requests(user_id);
CREATE INDEX idx_borrow_status ON borrow_requests(status);

CREATE INDEX idx_maintenance_asset ON asset_maintenance(asset_id);