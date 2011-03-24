package org.micropsi.eclipse.emotion3d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.micropsi.comp.messages.MTreeNode;

public class NodenetFaceTranslation implements IEmotionFaceTranslation {
	
	private List<String> faceParameterNames = new ArrayList<String>();
	
	public NodenetFaceTranslation() {
		faceParameterNames.add("Bone_Hals_oben_Rotation");
		faceParameterNames.add("Bone_Hals_oben_Neigung");
		faceParameterNames.add("Bone_Kiefer_Zwischengelenk");
		faceParameterNames.add("Bone_Unterkiefer");
		faceParameterNames.add("Bone_Kopf_Neigung");
		faceParameterNames.add("Bone_Kopf_Rotation");
		faceParameterNames.add("Bone_Oberlippe");
		faceParameterNames.add("Bone_Unterlippe");
		faceParameterNames.add("Bone_Zunge");
		faceParameterNames.add("L_Bone_Auge_horizontal");
		faceParameterNames.add("L_Bone_Auge_vertikal");
		faceParameterNames.add("R_Bone_Auge_horizontal");
		faceParameterNames.add("R_Bone_Auge_vertikal");
		faceParameterNames.add("L_Bone_Augenlid_oben");
		faceParameterNames.add("L_Bone_Augenlid_unten");
		faceParameterNames.add("L_Bone_Braue_aussen");
		faceParameterNames.add("L_Bone_Braue_innen");
		faceParameterNames.add("L_Bone_Braue_mitte");
		faceParameterNames.add("R_Bone_Augenlid_oben");
		faceParameterNames.add("R_Bone_Augenlid_unten");
		faceParameterNames.add("R_Bone_Braue_aussen");
		faceParameterNames.add("R_Bone_Braue_innen");
		faceParameterNames.add("R_Bone_Braue_mitte");
		faceParameterNames.add("L_Bone_Mund_Breite");
		faceParameterNames.add("R_Bone_Mund_Breite");
		faceParameterNames.add("L_Bone_Mund_oben");
		faceParameterNames.add("R_Bone_Mund_oben");
		faceParameterNames.add("L_Bone_Mundwinkel");
		faceParameterNames.add("R_Bone_Mundwinkel");
		faceParameterNames.add("L_Bone_Nasenfl_gel");
		faceParameterNames.add("R_Bone_Nadenfl_gel_");
		faceParameterNames.add("L_Bone_Wange");
		faceParameterNames.add("R_Bone_Wange");
	}
	
	public String getName() {
		return "Nodenet";
	}

	public List<String> getFaceParameterNames() {
		return faceParameterNames;
	}

	public double[] calculateFaceParameters(MTreeNode agentActorData) {
		double[] values = new double[33];
		
		for(int i=0;i<33;i++) {
			switch(i) {
			case 0: values[i] = getValueFromData("face_neck_upper_nod", agentActorData); 
			case 1: values[i] = getValueFromData("face_neck_upper_rotation",agentActorData); break;
			case 2: values[i] = getValueFromData("face_jaw_intermediate",agentActorData); break;
			case 3: values[i] = getValueFromData("face_jaw",agentActorData); break;
			case 4: values[i] = getValueFromData("face_head_nod",agentActorData); break;
			case 5: values[i] = getValueFromData("face_head_rotation",agentActorData); break;
			case 6: values[i] = getValueFromData("face_upper_lip",agentActorData); break;
			case 7: values[i] = getValueFromData("face_lower_lip",agentActorData); break;
			case 8: values[i] = getValueFromData("face_tongue",agentActorData); break;
			case 9: values[i] = getValueFromData("face_eye_left_h",agentActorData); break;
			case 10: values[i] = getValueFromData("face_eye_left_v",agentActorData); break;
			case 11: values[i] = getValueFromData("face_eye_right_h",agentActorData); break;
			case 12: values[i] = getValueFromData("face_eye_right_v",agentActorData); break;
			case 13: values[i] = getValueFromData("face_eyelid_left_upper",agentActorData); break;
			case 14: values[i] = getValueFromData("face_eyelid_left_lower",agentActorData); break;
			case 15: values[i] = getValueFromData("face_eyebrow_left_outer",agentActorData); break;
			case 16: values[i] = getValueFromData("face_eyebrow_left_inner",agentActorData); break;
			case 17: values[i] = getValueFromData("face_eyebrow_left_center",agentActorData); break;
			case 18: values[i] = getValueFromData("face_eyelid_right_upper",agentActorData); break;
			case 19: values[i] = getValueFromData("face_eyelid_right_lower",agentActorData); break;
			case 20: values[i] = getValueFromData("face_eyebrow_right_outer",agentActorData); break;
			case 21: values[i] = getValueFromData("face_eyebrow_right_inner",agentActorData); break;
			case 22: values[i] = getValueFromData("face_eyebrow_right_center",agentActorData); break;
			case 23: values[i] = getValueFromData("face_mouth_width_left",agentActorData); break;
			case 24: values[i] = getValueFromData("face_mouth_width_right",agentActorData); break;
			case 25: values[i] = getValueFromData("face_mouth_left_upper",agentActorData); break;
			case 26: values[i] = getValueFromData("face_mouth_right_upper",agentActorData); break;
			case 27: values[i] = getValueFromData("face_mouth_corner_left",agentActorData); break;
			case 28: values[i] = getValueFromData("face_mouth_corner_right",agentActorData); break;
			case 29: values[i] = getValueFromData("face_nose_wing_left",agentActorData); break;
			case 30: values[i] = getValueFromData("face_nose_wing_right",agentActorData); break;
			case 31: values[i] = getValueFromData("face_cheek_left",agentActorData); break;
			case 32: values[i] = getValueFromData("face_cheek_right",agentActorData); break;
			}
		}
		
		return values;
	}
	
	private double getValueFromData(String name, MTreeNode data) {
		try {
			Iterator<MTreeNode> children = data.children();
			while(children.hasNext()) {
				MTreeNode node = children.next();
				if(name.equals(node.getName())) {
					return Double.parseDouble(node.getValue());
				}
			}
		} catch(Exception e) {
			return 0.5;
		}
		return 0.5;
	}

}
