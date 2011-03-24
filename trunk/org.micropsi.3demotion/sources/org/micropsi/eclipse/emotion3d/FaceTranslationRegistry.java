package org.micropsi.eclipse.emotion3d;

import java.util.ArrayList;
import java.util.List;

public class FaceTranslationRegistry {

	private static FaceTranslationRegistry instance = new FaceTranslationRegistry();
	
	public static FaceTranslationRegistry getInstance() {
		return instance;
	}
	
	private List<IEmotionFaceTranslation> translations = new ArrayList<IEmotionFaceTranslation>();
	
	private FaceTranslationRegistry() {
	}

	public void registerEmotionFaceTranslation(IEmotionFaceTranslation translation) {
		translations.add(translation);
	}
	
	public void unregisterEmotionFaceTranslation(IEmotionFaceTranslation translation) {
		translations.remove(translation);
	}
	
	public List<IEmotionFaceTranslation> getEmotionFaceTranslations() {
		return translations;
	}
	
}
