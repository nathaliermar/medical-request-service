-- Integration test seed data
-- Inserted before each integration test via @Sql
-- version column required by @Version optimistic locking on MedicalRequestEntity

INSERT INTO medical_requests (id, beneficiary_id, description, status, created_at, updated_at, version)
VALUES
  ('a1000000-0000-0000-0000-000000000001',
   'b1000000-0000-0000-0000-000000000001',
   'Request for cardiology consultation',
   'DRAFT', NOW(), NOW(), 0),

  ('a1000000-0000-0000-0000-000000000002',
   'b1000000-0000-0000-0000-000000000001',
   'Request for orthopaedic surgery',
   'SUBMITTED', NOW(), NOW(), 0),

  ('a1000000-0000-0000-0000-000000000003',
   'b1000000-0000-0000-0000-000000000002',
   'MRI scan request',
   'SUBMITTED', NOW(), NOW(), 0),

  ('a1000000-0000-0000-0000-000000000004',
   'b1000000-0000-0000-0000-000000000003',
   'Concurrent update test request',
   'DRAFT', NOW(), NOW(), 0);

INSERT INTO medical_procedures (id, request_id, icd_code, cbo_code, description, quantity, coverage_approved)
VALUES
  ('c1000000-0000-0000-0000-000000000001',
   'a1000000-0000-0000-0000-000000000001',
   'I10', '225142', 'Cardiology consultation', 1, TRUE),

  ('c1000000-0000-0000-0000-000000000002',
   'a1000000-0000-0000-0000-000000000002',
   'M17.1', '225270', 'Knee arthroplasty', 1, TRUE),

  ('c1000000-0000-0000-0000-000000000003',
   'a1000000-0000-0000-0000-000000000003',
   'G35', '225142', 'Brain MRI for multiple sclerosis', 1, TRUE),

  ('c1000000-0000-0000-0000-000000000004',
   'a1000000-0000-0000-0000-000000000004',
   'I10', '225142', 'Hypertension follow-up', 1, TRUE);
