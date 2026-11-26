-- Flyway migration: create teleconsultations table

CREATE TABLE IF NOT EXISTS teleconsultations (
  id            SERIAL PRIMARY KEY,
  owner_id      INTEGER NOT NULL,
  pet_id        INTEGER NOT NULL,
  vet_id        INTEGER NULL,
  contact_method VARCHAR(16) NOT NULL,
  status        VARCHAR(24) NOT NULL,
  start_ts      TIMESTAMPTZ NOT NULL,
  end_ts        TIMESTAMPTZ NOT NULL,
  meeting_url   VARCHAR(512) NULL,
  notes         VARCHAR(2048) NULL,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  version       INTEGER NOT NULL DEFAULT 0,

  CONSTRAINT fk_teleconsultation_owner FOREIGN KEY (owner_id) REFERENCES owners(id) ON DELETE CASCADE,
  CONSTRAINT fk_teleconsultation_pet   FOREIGN KEY (pet_id)   REFERENCES pets(id)   ON DELETE CASCADE,
  CONSTRAINT fk_teleconsultation_vet   FOREIGN KEY (vet_id)   REFERENCES vets(id)   ON DELETE SET NULL,
  CONSTRAINT chk_teleconsultation_time CHECK (end_ts > start_ts)
);

CREATE INDEX IF NOT EXISTS idx_teleconsultations_owner_id ON teleconsultations(owner_id);
CREATE INDEX IF NOT EXISTS idx_teleconsultations_pet_id   ON teleconsultations(pet_id);
CREATE INDEX IF NOT EXISTS idx_teleconsultations_vet_id   ON teleconsultations(vet_id);
CREATE INDEX IF NOT EXISTS idx_teleconsultations_start_ts ON teleconsultations(start_ts);
