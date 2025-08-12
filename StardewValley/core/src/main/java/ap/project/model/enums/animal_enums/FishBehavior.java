package ap.project.model.enums.animal_enums;

public enum FishBehavior {
    SMOOTH(1.0f), SINKER(0.8f), FLOATER(0.8f), DART(1.2f), MIXED(1.1f);

    public final float difficultyModifier;

    FishBehavior(float difficultyModifier) {
        this.difficultyModifier = difficultyModifier;
    }

    public float getDifficultyModifier() {
        return difficultyModifier;
    }
}
