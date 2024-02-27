BEGIN;

CREATE TABLE o_user (
   id SERIAL PRIMARY KEY,
   email VARCHAR(100) NOT NULL UNIQUE,
   name VARCHAR(100) NOT NULL,
   image TEXT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE goal (
   id SERIAL PRIMARY KEY,
   user_id INT REFERENCES o_user(id) NOT NULL,
   type TEXT NOT NULL CHECK (type IN ('Weight', 'Nutrition', 'Fitness')),
   goal JSONB NOT NULL,
   UNIQUE (user_id, type)
);

CREATE TABLE health_indicator_history (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES o_user(id) NOT NULL,
    created_at DATE NOT NULL,
    indicators JSONB NOT NULL,
    UNIQUE (user_id, created_at)
);

CREATE TABLE food (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES o_user(id) NOT NULL,
    name VARCHAR(200) NOT NULL,
    calories DECIMAL NOT NULL,
    nutrients JSONB NOT NULL,
    type TEXT NOT NULL CHECK (type IN ('Breakfast', 'Lunch', 'Dinner'))
);

CREATE TABLE food_log (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES o_user(id) NOT NULL,
    food_id INT REFERENCES food(id) ON DELETE CASCADE NOT NULL,
    created_at DATE NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    UNIQUE (user_id, food_id, created_at)
);

CREATE TABLE exercise (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES o_user(id) NOT NULL,
    type TEXT NOT NULL CHECK (type IN ('Cardiovascular', 'Strength')),
    name VARCHAR(200) NOT NULL,
    duration INT NOT NULL,
    details JSONB NOT NULL,
    calories_burned DECIMAL
);

CREATE TABLE exercise_log (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES o_user(id) NOT NULL,
    exercise_id INT REFERENCES exercise(id) ON DELETE CASCADE NOT NULL,
    created_at DATE NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    UNIQUE (user_id, exercise_id, created_at)
);

CREATE TABLE note (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES o_user(id) NOT NULL,
    created_at Date NOT NULL,
    type TEXT NOT NULL CHECK (type IN ('Food', 'Exercise')),
    description TEXT NOT NULL,
    UNIQUE (user_id, created_at, type)
);

CREATE OR REPLACE FUNCTION update_weight_history_on_goal_patch()
    RETURNS TRIGGER AS $$
    DECLARE
        new_weight DECIMAL;
        record_exists BOOLEAN;
    BEGIN
        IF NEW.type = 'Weight' THEN
            new_weight := (NEW.goal->>'current_weight')::DECIMAL;

           SELECT EXISTS (
	            SELECT 1 FROM health_indicator_history 
	            WHERE user_id = NEW.user_id 
	            AND created_at = CURRENT_DATE
        	) INTO record_exists;

            IF record_exists THEN
			    UPDATE health_indicator_history
			    SET indicators = jsonb_set(indicators, '{current_weight}', to_jsonb(new_weight::decimal), true)
			    WHERE user_id = NEW.user_id AND created_at = CURRENT_DATE;
			ELSE
			    INSERT INTO health_indicator_history (user_id, created_at, indicators)
			    VALUES (NEW.user_id, CURRENT_DATE, jsonb_build_object('current_weight', new_weight));
			END IF;
        END IF;

        RETURN NEW;
    END;
    $$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trigger_update_weight_on_goal_patch1
AFTER INSERT OR UPDATE OF goal ON goal
FOR EACH ROW
EXECUTE FUNCTION update_weight_history_on_goal_patch();
    
CREATE OR REPLACE FUNCTION update_jsonb(
    table_name TEXT,
    jsonb_column TEXT,
    id_value INT,
    new_values JSONB
)
RETURNS SETOF jsonb AS $$
DECLARE
    updates JSONB := '{}'::JSONB;
    rec RECORD;
    query TEXT;
BEGIN
    FOR rec IN SELECT * FROM jsonb_each(new_values)
    LOOP
        IF rec.value::TEXT != 'null' THEN
            updates := updates || jsonb_build_object(rec.key, rec.value);
        END IF;
    END LOOP;

    IF updates != '{}'::JSONB THEN
        query := format('
            UPDATE %I 
            SET %I = %I || $1
            WHERE id = $2
            RETURNING %I;',
            table_name,
            jsonb_column,
            jsonb_column,
            jsonb_column
        );
        RETURN QUERY EXECUTE query USING updates, id_value;
    END IF;
END;
$$ LANGUAGE plpgsql;

COMMIT;