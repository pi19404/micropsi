#include "stdafx.h"
#include "GameLib/Utilities/XMLUtils.h"
#include "e42/core/Model.h"
#include "e42/AnimationCtrl.h"

#include <string>
using std::string;

#include "tinyxml.h"

namespace XMLUtils
{
//---------------------------------------------------------------------------------------------------------------------
void
ReadAnimationsFromXML(TiXmlElement* pxAnimationsElement, const TModelHandle& hndModel, CAnimationCtrl* pxAnimationCtrl)
{
    TiXmlElement* pxAnimation = pxAnimationsElement->FirstChildElement();
	while (pxAnimation)
	{
		string sAnimName = pxAnimation->Value();

		TiXmlNode* pxSubNode = pxAnimation->FirstChild();
		while (pxSubNode)
		{
            switch (pxSubNode->Type())
            {
            case TiXmlNode::TEXT :
                if (hndModel)
                {
					string sAnimFilename = string("animation>") + GetXMLTagString(pxAnimationsElement, sAnimName);
                    hndModel->AddAnimation(sAnimFilename, sAnimName);
			    }
                break;

            case TiXmlNode::ELEMENT :
				if(string(pxSubNode->ToElement()->Value()) == "sound")
					pxAnimationCtrl->AddSound(sAnimName, pxSubNode->ToElement());
                else
                    assert(false);
                break;

            default:
                assert(false);
            }

			pxSubNode = pxSubNode->NextSibling();
		}

		pxAnimation = pxAnimation->NextSiblingElement();
	}
}
//---------------------------------------------------------------------------------------------------------------------

}  // namespace XMLUtils
