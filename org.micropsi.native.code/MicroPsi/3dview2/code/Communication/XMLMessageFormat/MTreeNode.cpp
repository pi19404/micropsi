#include "Application/stdinc.h"
#include "MTreeNode.h"

#include "baselib/str.h"
#include "tinyxml.h"

using std::string;

//---------------------------------------------------------------------------------------------------------------------
CMTreeNode::CMTreeNode()
{
}

//---------------------------------------------------------------------------------------------------------------------
CMTreeNode::CMTreeNode(std::string p_sKey, std::string p_sValue)
{
	m_sKey		= p_sKey;
	m_sValue	= p_sValue;
}

//---------------------------------------------------------------------------------------------------------------------
CMTreeNode::~CMTreeNode()
{
}

//---------------------------------------------------------------------------------------------------------------------
CMessage::MessageType		
CMTreeNode::GetMessageType() const
{
	return MTYPE_CONSOLE_TREENODE;
}

//---------------------------------------------------------------------------------------------------------------------
string		
CMTreeNode::ToXMLString() const
{
	string sChildren;
	std::vector<CMTreeNode>::const_iterator i;
	for(i=m_axChildren.begin(); i!=m_axChildren.end(); i++)
	{
		sChildren += i->ToXMLString();
	}

	CStr sRes = CStr::Create("<m%d key=\"%s\" value=\"%s\">%s</m%d>", 
		MTYPE_CONSOLE_TREENODE, m_sKey.c_str(), m_sValue.c_str(), sChildren.c_str(), MTYPE_CONSOLE_TREENODE); 

	return sRes.c_str();
}

//---------------------------------------------------------------------------------------------------------------------
bool		
CMTreeNode::FromXMLElement(const TiXmlElement* p_pxXMLElement)
{
	if(string(p_pxXMLElement->Value()) != "m304")
	{
		assert(false);
		return false;
	}

	// parse attributes

	const TiXmlAttribute* pxAttrib = p_pxXMLElement->FirstAttribute();
	while(pxAttrib)
	{
		string sName = pxAttrib->Name();
		if(sName == "name")
		{
			m_sKey = pxAttrib->Value();
		}
		else if(sName == "value")
		{
			m_sValue = pxAttrib->Value();
		}


		pxAttrib = pxAttrib->Next();
	}

	// parse children

	const TiXmlElement* pxElement = p_pxXMLElement->FirstChildElement();
	while(pxElement)
	{
		string sName = pxElement->Value();
		if(sName == "m304")		// treenode
		{
			m_axChildren.push_back(CMTreeNode());
			m_axChildren[m_axChildren.size() -1].FromXMLElement(pxElement);
		}
		else
		{
			DebugPrint("Warning: CMConsoleResponse::FromXMLElement(): Message Type %s - parsing not implemented", sName.c_str());
		}

		pxElement = pxElement->NextSiblingElement();
	}

	return false;
}
//---------------------------------------------------------------------------------------------------------------------
