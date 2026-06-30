-- ============================================================
-- TheKnife - Creazione struttura DB (VERSIONE CORRETTA)
-- Esegui questo in pgAdmin sul database theknife_db
-- ============================================================

-- Elimina le tabelle se esistono (ordine corretto per le foreign key)
DROP TABLE IF EXISTS Preferiti CASCADE;
DROP TABLE IF EXISTS Risposte CASCADE;
DROP TABLE IF EXISTS Recensioni CASCADE;
DROP TABLE IF EXISTS Proprietari CASCADE;
DROP TABLE IF EXISTS RistorantiTheKnife CASCADE;
DROP TABLE IF EXISTS Utenti CASCADE;

-- ============================================================
-- TABELLA: Utenti
-- ============================================================
CREATE TABLE Utenti (
    id               SERIAL PRIMARY KEY,
    nome             VARCHAR(100) NOT NULL,
    cognome          VARCHAR(100) NOT NULL,
    username         VARCHAR(100) NOT NULL UNIQUE,
    password         VARCHAR(255) NOT NULL,
    data_nascita     VARCHAR(20),
    luogo_domicilio  VARCHAR(150) NOT NULL,
    ruolo            VARCHAR(20)  NOT NULL CHECK (ruolo IN ('cliente', 'ristoratore'))
);

-- ============================================================
-- TABELLA: RistorantiTheKnife
-- ============================================================
CREATE TABLE RistorantiTheKnife (
    id            SERIAL PRIMARY KEY,
    nome          VARCHAR(255)     NOT NULL,
    indirizzo     VARCHAR(300)     NOT NULL,
    citta         VARCHAR(150)     NOT NULL,
    nazione       VARCHAR(100)     NOT NULL,
    prezzo        VARCHAR(20)      NOT NULL,
    tipo_cucina   VARCHAR(200)     NOT NULL,
    longitudine   DOUBLE PRECISION NOT NULL,
    latitudine    DOUBLE PRECISION NOT NULL,
    delivery      BOOLEAN          NOT NULL DEFAULT FALSE,
    prenotazione  BOOLEAN          NOT NULL DEFAULT FALSE,
    fonte         VARCHAR(20)      NOT NULL DEFAULT 'michelin',
    CHECK (fonte IN ('michelin', 'custom')),
    UNIQUE (nome, citta, indirizzo)
);

-- ============================================================
-- TABELLA: Proprietari (associazione ristoratore -> ristorante)
-- ============================================================
CREATE TABLE Proprietari (
    id              SERIAL PRIMARY KEY,
    utente_id       INTEGER NOT NULL REFERENCES Utenti(id) ON DELETE CASCADE,
    ristorante_id   INTEGER NOT NULL REFERENCES RistorantiTheKnife(id) ON DELETE CASCADE,
    UNIQUE (utente_id, ristorante_id)
);

-- ============================================================
-- TABELLA: Recensioni
-- ============================================================
CREATE TABLE Recensioni (
    id            SERIAL PRIMARY KEY,
    ristorante_id INTEGER  NOT NULL REFERENCES RistorantiTheKnife(id) ON DELETE CASCADE,
    utente_id     INTEGER  NOT NULL REFERENCES Utenti(id) ON DELETE CASCADE,
    testo         TEXT,
    stelle        SMALLINT NOT NULL CHECK (stelle >= 1 AND stelle <= 5),
    data_rec      DATE     NOT NULL DEFAULT CURRENT_DATE,
    UNIQUE (ristorante_id, utente_id)
);

-- ============================================================
-- TABELLA: Risposte (risposte del ristoratore alle recensioni)
-- ============================================================
CREATE TABLE Risposte (
    id            SERIAL PRIMARY KEY,
    recensione_id INTEGER NOT NULL REFERENCES Recensioni(id) ON DELETE CASCADE,
    utente_id     INTEGER NOT NULL REFERENCES Utenti(id) ON DELETE CASCADE,
    testo         TEXT    NOT NULL,
    data_risp     DATE    NOT NULL DEFAULT CURRENT_DATE,
    UNIQUE (recensione_id)
);

-- ============================================================
-- TABELLA: Preferiti
-- ============================================================
CREATE TABLE Preferiti (
    id            SERIAL PRIMARY KEY,
    utente_id     INTEGER NOT NULL REFERENCES Utenti(id) ON DELETE CASCADE,
    ristorante_id INTEGER NOT NULL REFERENCES RistorantiTheKnife(id) ON DELETE CASCADE,
    UNIQUE (utente_id, ristorante_id)
);

-- ============================================================
-- INDICI per migliorare le performance delle ricerche
-- ============================================================
CREATE INDEX idx_ristoranti_citta   ON RistorantiTheKnife(citta);
CREATE INDEX idx_ristoranti_nome    ON RistorantiTheKnife(nome);
CREATE INDEX idx_ristoranti_cucina  ON RistorantiTheKnife(tipo_cucina);
CREATE INDEX idx_recensioni_rist    ON Recensioni(ristorante_id);
CREATE INDEX idx_recensioni_utente  ON Recensioni(utente_id);
CREATE INDEX idx_preferiti_utente   ON Preferiti(utente_id);
CREATE INDEX idx_proprietari_utente ON Proprietari(utente_id);
CREATE INDEX idx_proprietari_rist   ON Proprietari(ristorante_id);

-- ============================================================
-- VERIFICA: mostra tutte le tabelle create
-- ============================================================
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
ORDER BY table_name;

-- ============================================================
-- VERIFICA: conta le righe in ogni tabella (dovrebbero essere 0)
-- ============================================================
SELECT 'Utenti' AS tabella, COUNT(*) AS righe FROM Utenti
UNION ALL
SELECT 'RistorantiTheKnife', COUNT(*) FROM RistorantiTheKnife
UNION ALL
SELECT 'Proprietari', COUNT(*) FROM Proprietari
UNION ALL
SELECT 'Recensioni', COUNT(*) FROM Recensioni
UNION ALL
SELECT 'Risposte', COUNT(*) FROM Risposte
UNION ALL
SELECT 'Preferiti', COUNT(*) FROM Preferiti;
