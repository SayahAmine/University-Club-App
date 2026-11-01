

CREATE TABLE events (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                        title VARCHAR(200) NOT NULL,
                        description TEXT,
                        location VARCHAR(300),

                        start_at TIMESTAMP NOT NULL,
                        end_at TIMESTAMP NOT NULL,
                        registration_deadline TIMESTAMP,

                        capacity INTEGER,

                        requires_approval BOOLEAN NOT NULL DEFAULT FALSE,

                        status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',

                        created_by UUID NOT NULL REFERENCES users(id),

                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                        CONSTRAINT chk_event_dates
                            CHECK (end_at > start_at),

                        CONSTRAINT chk_event_capacity
                            CHECK (capacity IS NULL OR capacity > 0),

                        CONSTRAINT chk_event_status
                            CHECK (status IN (
                                              'DRAFT',
                                              'PUBLISHED',
                                              'CANCELLED',
                                              'COMPLETED'
                                ))
);



CREATE TABLE event_registrations (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                                     event_id UUID NOT NULL REFERENCES events(id),
                                     user_id UUID NOT NULL REFERENCES users(id),

                                     status VARCHAR(30) NOT NULL DEFAULT 'PENDING',

                                     attendance_status VARCHAR(30) NOT NULL DEFAULT 'NOT_MARKED',

                                     registered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                     reviewed_by UUID REFERENCES users(id),
                                     reviewed_at TIMESTAMP,

                                     attendance_marked_by UUID REFERENCES users(id),
                                     attendance_marked_at TIMESTAMP,

                                     notes TEXT,

                                     CONSTRAINT uq_event_user
                                         UNIQUE (event_id, user_id),

                                     CONSTRAINT chk_registration_status
                                         CHECK (status IN (
                                                           'PENDING',
                                                           'APPROVED',
                                                           'REJECTED',
                                                           'CANCELLED'
                                             )),

                                     CONSTRAINT chk_attendance_status
                                         CHECK (attendance_status IN (
                                                                      'NOT_MARKED',
                                                                      'PRESENT',
                                                                      'ABSENT'
                                             ))
);



CREATE INDEX idx_events_status_start
    ON events(status, start_at);

CREATE INDEX idx_events_registration_deadline
    ON events(registration_deadline);

CREATE INDEX idx_events_created_by
    ON events(created_by);

CREATE INDEX idx_event_registrations_event
    ON event_registrations(event_id);

CREATE INDEX idx_event_registrations_user
    ON event_registrations(user_id);

CREATE INDEX idx_event_registrations_status
    ON event_registrations(event_id, status);