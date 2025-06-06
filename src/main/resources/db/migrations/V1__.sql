CREATE TABLE board_column_entity
(
    id       BIGINT GENERATED ALWAYS AS IDENTITY  PRIMARY KEY,
    name     VARCHAR(255),
    columnorder  INT    NOT NULL,
    kind     VARCHAR(10),
    board_id BIGINT
);

CREATE TABLE board_entity
(
    id       BIGINT GENERATED ALWAYS AS IDENTITY  PRIMARY KEY,
    name VARCHAR(255)
);


CREATE TABLE card_entity
(
    id       BIGINT GENERATED ALWAYS AS IDENTITY  PRIMARY KEY,
    title           VARCHAR(255),
    description     VARCHAR(255),
    board_column_id BIGINT

);


CREATE TABLE block_entity
(
    id       BIGINT GENERATED ALWAYS AS IDENTITY  PRIMARY KEY,
    blocked_at    TIMESTAMP,
    block_reason  VARCHAR(255),
    unblocked_at  TIMESTAMP,
    unblock_reason VARCHAR(255),
    card_id     BIGINT NOT NULL

);


ALTER TABLE board_column_entity
    ADD CONSTRAINT FK_BOARDCOLUMNENTITY_ON_BOARD FOREIGN KEY (board_id) REFERENCES board_entity (id);

ALTER TABLE card_entity
    ADD CONSTRAINT FK_CARDENTITY_ON_BOARDCOLUMN FOREIGN KEY (board_column_id) REFERENCES board_column_entity (id);


INSERT INTO board_entity (name) VALUES ( 'Sprint Planning Q3');
INSERT INTO board_entity (name) VALUES ( 'Website V2 Launch');
INSERT INTO board_entity (name) VALUES ('Mobile App Dev');
INSERT INTO board_entity (name) VALUES ('Client Project Nova');
INSERT INTO board_entity (name) VALUES ('HR Onboarding Flow');
INSERT INTO board_entity (name) VALUES ('Cloud Migration Phase 1');
INSERT INTO board_entity (name) VALUES ('Bug Tracking Board');
INSERT INTO board_entity (name) VALUES ('Content Marketing Ideas');
INSERT INTO board_entity (name) VALUES ('Infrastructure Upgrade');
INSERT INTO board_entity (name) VALUES ('Financial Reporting 2025');

INSERT INTO board_column_entity (name, columnorder, kind, board_id) VALUES ( 'Backlog Items', 0, 'INITIAL', 1);    -- Board 1
INSERT INTO board_column_entity (name, columnorder, kind, board_id) VALUES ( 'In Progress Frontend', 1, 'PENDING', 2); -- Board 2
INSERT INTO board_column_entity (name, columnorder, kind, board_id) VALUES ( 'Code Review', 2, 'PENDING', 3);    -- Board 3
INSERT INTO board_column_entity (name, columnorder, kind, board_id) VALUES ( 'Deployed', 3, 'FINAL', 4);     -- Board 4
INSERT INTO board_column_entity (name, columnorder, kind, board_id) VALUES ( 'On Hold', 4, 'CANCEL', 5);  -- Board 5
INSERT INTO board_column_entity (name, columnorder, kind, board_id) VALUES ( 'New Requests', 0, 'INITIAL', 6);   -- Board 6
INSERT INTO board_column_entity (name, columnorder, kind, board_id) VALUES ( 'UAT Testing', 1, 'PENDING', 7);   -- Board 7
INSERT INTO board_column_entity (name, columnorder, kind, board_id) VALUES ( 'Archived', 2, 'FINAL', 8); -- Board 8
INSERT INTO board_column_entity (name, columnorder, kind, board_id) VALUES ( 'Invalid', 3, 'CANCEL', 9); -- Board 9
INSERT INTO board_column_entity (name, columnorder, kind, board_id) VALUES ( 'Discovery', 0, 'INITIAL', 10);-- Board 10
INSERT INTO board_column_entity (name, columnorder, kind, board_id) VALUES ( 'In Progress Backend', 1, 'PENDING', 1); -- Board 1
INSERT INTO board_column_entity (name, columnorder, kind, board_id) VALUES ( 'QA Testing', 2, 'PENDING', 2);         -- Board 2
INSERT INTO board_column_entity (name, columnorder, kind, board_id) VALUES ( 'Done', 2, 'FINAL', 1);             -- Board 1
INSERT INTO board_column_entity (name, columnorder, kind, board_id) VALUES ( 'Launch Prep', 3, 'PENDING', 2);        -- Board 2

INSERT INTO card_entity ( title, description, board_column_id) VALUES ('Refine Q3 User Stories', 'Break down epic into smaller, actionable stories.', 1);
INSERT INTO card_entity ( title, description, board_column_id) VALUES ( 'Develop Homepage Layout', 'Implement responsive design for desktop and mobile.', 2);
INSERT INTO card_entity ( title, description, board_column_id) VALUES ( 'Integrate Payment Gateway', 'Connect Stripe API for secure transactions.', 3);
INSERT INTO card_entity ( title, description, board_column_id) VALUES ( 'Final Client Demo Prep', 'Prepare presentation and ensure all features are working.', 4);
INSERT INTO card_entity ( title, description, board_column_id) VALUES ( 'Create Welcome Email Series', 'Draft and automate emails for new employees.', 5);
INSERT INTO card_entity ( title, description, board_column_id) VALUES ( 'Assess Server Capacity', 'Monitor current server usage and plan for scaling.', 6);
INSERT INTO card_entity ( title, description, board_column_id) VALUES ( 'Verify Data Integrity', 'Run automated tests on database integrity after migration.', 7);
INSERT INTO card_entity ( title, description, board_column_id) VALUES ( 'Brainstorm Blog Post Topics', 'Generate ideas for the next 3 months of blog content.', 8);
INSERT INTO card_entity ( title, description, board_column_id) VALUES ( 'Upgrade Network Switches', 'Replace outdated network hardware in server room.', 9);
INSERT INTO card_entity ( title, description, board_column_id) VALUES ( 'Generate Monthly P&L Report', 'Compile financial data and prepare for review.', 10);
INSERT INTO card_entity ( title, description, board_column_id) VALUES ( 'Prioritize next sprint tasks', 'Review backlog and select items for upcoming sprint.', 1);
INSERT INTO card_entity ( title, description, board_column_id) VALUES ( 'Estimate story points', 'Assign effort estimates to selected user stories.', 1);
INSERT INTO card_entity ( title, description, board_column_id) VALUES ( 'Develop user authentication API', 'Implement JWT-based authentication endpoints.', 11);
INSERT INTO card_entity ( title, description, board_column_id) VALUES ( 'Build payment processing module', 'Integrate with third-party payment provider.', 11);
INSERT INTO card_entity ( title, description, board_column_id) VALUES ( 'Implement responsive navbar', 'Ensure navigation bar adapts to different screen sizes.', 2);
INSERT INTO card_entity ( title, description, board_column_id) VALUES ( 'Create product detail page', 'Display product information and add-to-cart button.', 2);
INSERT INTO card_entity ( title, description, board_column_id) VALUES ( 'Perform end-to-end testing', 'Test entire user journey from login to checkout.', 12);
INSERT INTO card_entity ( title, description, board_column_id) VALUES ( 'Execute regression tests', 'Verify no existing functionality is broken by new changes.', 12);
INSERT INTO card_entity ( title, description, board_column_id) VALUES ( 'Final documentation review', 'Review all project documentation before release.', 13);
INSERT INTO card_entity ( title, description, board_column_id) VALUES ( 'Coordinate marketing campaign', 'Plan social media and email blasts for launch.', 14);
