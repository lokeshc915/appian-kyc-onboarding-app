
ALTER TABLE onboarding_case
  ADD COLUMN account_country VARCHAR(120) NULL AFTER account_zip,
  ADD COLUMN account_risk_level VARCHAR(40) NULL AFTER account_country;
