CREATE TABLE board_column_entity
(
    id       BIGINT NOT NULL,
    name     VARCHAR(255),
    columnorder  INT    NOT NULL,
    kind     VARCHAR(10),
    board_id BIGINT,
    CONSTRAINT pk_boardcolumnentity PRIMARY KEY (id)
);

CREATE TABLE board_entity
(
    id   BIGINT NOT NULL,
    name VARCHAR(255),
    CONSTRAINT pk_boardentity PRIMARY KEY (id)
);


CREATE TABLE card_entity
(
    id              BIGINT NOT NULL,
    title           VARCHAR(255),
    description     VARCHAR(255),
    board_column_id BIGINT,
    CONSTRAINT pk_cardentity PRIMARY KEY (id)
);


CREATE TABLE block_entity
(
    id            BIGINT NOT NULL,
    blocked_at    TIMESTAMP,
    block_reason  VARCHAR(255),
    unblocked_at  TIMESTAMP,
    unblock_reason VARCHAR(255),
    card_id     BIGINT NOT NULL,
    CONSTRAINT pk_blockentity PRIMARY KEY (id)
);


ALTER TABLE board_column_entity
    ADD CONSTRAINT FK_BOARDCOLUMNENTITY_ON_BOARD FOREIGN KEY (board_id) REFERENCES board_entity (id);

ALTER TABLE card_entity
    ADD CONSTRAINT FK_CARDENTITY_ON_BOARDCOLUMN FOREIGN KEY (board_column_id) REFERENCES board_column_entity (id);


INSERT INTO board_entity (id, name) VALUES (1, 'Sprint Planning Q3');
INSERT INTO board_entity (id, name) VALUES (2, 'Website V2 Launch');
INSERT INTO board_entity (id, name) VALUES (3, 'Mobile App Dev');
INSERT INTO board_entity (id, name) VALUES (4, 'Client Project Nova');
INSERT INTO board_entity (id, name) VALUES (5, 'HR Onboarding Flow');
INSERT INTO board_entity (id, name) VALUES (6, 'Cloud Migration Phase 1');
INSERT INTO board_entity (id, name) VALUES (7, 'Bug Tracking Board');
INSERT INTO board_entity (id, name) VALUES (8, 'Content Marketing Ideas');
INSERT INTO board_entity (id, name) VALUES (9, 'Infrastructure Upgrade');
INSERT INTO board_entity (id, name) VALUES (10, 'Financial Reporting 2025');

INSERT INTO board_column_entity (id, name, columnorder, kind, board_id) VALUES (101, 'Backlog Items', 0, 'INITIAL', 1);    -- Board 1
INSERT INTO board_column_entity (id, name, columnorder, kind, board_id) VALUES (102, 'In Progress Frontend', 1, 'PENDING', 2); -- Board 2
INSERT INTO board_column_entity (id, name, columnorder, kind, board_id) VALUES (103, 'Code Review', 2, 'PENDING', 3);    -- Board 3
INSERT INTO board_column_entity (id, name, columnorder, kind, board_id) VALUES (104, 'Deployed', 3, 'FINAL', 4);     -- Board 4
INSERT INTO board_column_entity (id, name, columnorder, kind, board_id) VALUES (105, 'On Hold', 4, 'CANCEL', 5);  -- Board 5
INSERT INTO board_column_entity (id, name, columnorder, kind, board_id) VALUES (106, 'New Requests', 0, 'INITIAL', 6);   -- Board 6
INSERT INTO board_column_entity (id, name, columnorder, kind, board_id) VALUES (107, 'UAT Testing', 1, 'PENDING', 7);   -- Board 7
INSERT INTO board_column_entity (id, name, columnorder, kind, board_id) VALUES (108, 'Archived', 2, 'FINAL', 8); -- Board 8
INSERT INTO board_column_entity (id, name, columnorder, kind, board_id) VALUES (109, 'Invalid', 3, 'CANCEL', 9); -- Board 9
INSERT INTO board_column_entity (id, name, columnorder, kind, board_id) VALUES (110, 'Discovery', 0, 'INITIAL', 10);-- Board 10

INSERT INTO board_column_entity (id, name, columnorder, kind, board_id) VALUES (111, 'In Progress Backend', 1, 'PENDING', 1); -- Board 1
INSERT INTO board_column_entity (id, name, columnorder, kind, board_id) VALUES (112, 'QA Testing', 2, 'PENDING', 2);         -- Board 2
INSERT INTO board_column_entity (id, name, columnorder, kind, board_id) VALUES (113, 'Done', 2, 'FINAL', 1);             -- Board 1
INSERT INTO board_column_entity (id, name, columnorder, kind, board_id) VALUES (114, 'Launch Prep', 3, 'PENDING', 2);        -- Board 2

INSERT INTO card_entity (id, title, description, board_column_id) VALUES (201, 'Refine Q3 User Stories', 'Break down epic into smaller, actionable stories.', 101);
INSERT INTO card_entity (id, title, description, board_column_id) VALUES (202, 'Develop Homepage Layout', 'Implement responsive design for desktop and mobile.', 102);
INSERT INTO card_entity (id, title, description, board_column_id) VALUES (203, 'Integrate Payment Gateway', 'Connect Stripe API for secure transactions.', 103);
INSERT INTO card_entity (id, title, description, board_column_id) VALUES (204, 'Final Client Demo Prep', 'Prepare presentation and ensure all features are working.', 104);
INSERT INTO card_entity (id, title, description, board_column_id) VALUES (205, 'Create Welcome Email Series', 'Draft and automate emails for new employees.', 105);
INSERT INTO card_entity (id, title, description, board_column_id) VALUES (206, 'Assess Server Capacity', 'Monitor current server usage and plan for scaling.', 106);
INSERT INTO card_entity (id, title, description, board_column_id) VALUES (207, 'Verify Data Integrity', 'Run automated tests on database integrity after migration.', 107);
INSERT INTO card_entity (id, title, description, board_column_id) VALUES (208, 'Brainstorm Blog Post Topics', 'Generate ideas for the next 3 months of blog content.', 108);
INSERT INTO card_entity (id, title, description, board_column_id) VALUES (209, 'Upgrade Network Switches', 'Replace outdated network hardware in server room.', 109);
INSERT INTO card_entity (id, title, description, board_column_id) VALUES (210, 'Generate Monthly P&L Report', 'Compile financial data and prepare for review.', 110);
INSERT INTO card_entity (id, title, description, board_column_id) VALUES (211, 'Prioritize next sprint tasks', 'Review backlog and select items for upcoming sprint.', 101);
INSERT INTO card_entity (id, title, description, board_column_id) VALUES (212, 'Estimate story points', 'Assign effort estimates to selected user stories.', 101);
INSERT INTO card_entity (id, title, description, board_column_id) VALUES (213, 'Develop user authentication API', 'Implement JWT-based authentication endpoints.', 111);
INSERT INTO card_entity (id, title, description, board_column_id) VALUES (214, 'Build payment processing module', 'Integrate with third-party payment provider.', 111);
INSERT INTO card_entity (id, title, description, board_column_id) VALUES (215, 'Implement responsive navbar', 'Ensure navigation bar adapts to different screen sizes.', 102);
INSERT INTO card_entity (id, title, description, board_column_id) VALUES (216, 'Create product detail page', 'Display product information and add-to-cart button.', 102);
INSERT INTO card_entity (id, title, description, board_column_id) VALUES (217, 'Perform end-to-end testing', 'Test entire user journey from login to checkout.', 112);
INSERT INTO card_entity (id, title, description, board_column_id) VALUES (218, 'Execute regression tests', 'Verify no existing functionality is broken by new changes.', 112);
INSERT INTO card_entity (id, title, description, board_column_id) VALUES (219, 'Final documentation review', 'Review all project documentation before release.', 113);
INSERT INTO card_entity (id, title, description, board_column_id) VALUES (220, 'Coordinate marketing campaign', 'Plan social media and email blasts for launch.', 114);
