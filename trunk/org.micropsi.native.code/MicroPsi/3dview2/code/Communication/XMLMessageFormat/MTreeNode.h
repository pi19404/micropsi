#ifndef MTREENODE_H_INCLUDED
#define MTREENODE_H_INCLUDED

#include "Message.h"
#include <string>
#include <vector>

class CMTreeNode : public CMessage
{
public:
	CMTreeNode();
	CMTreeNode(std::string p_sKey, std::string p_sValue);
	virtual ~CMTreeNode();
    
	virtual MessageType				GetMessageType() const;
	virtual std::string				ToXMLString() const;

	virtual bool					FromXMLElement(const TiXmlElement* p_pxXMLElement);

	const std::string&				GetKey() const;
	const std::string&				GetValue() const;
	const std::vector<CMTreeNode>&	GetChildren() const;	

private:

	std::string			m_sKey;
	std::string			m_sValue;

	std::vector<CMTreeNode>			m_axChildren;			
};

#include "MTreeNode.inl"

#endif // MTREENODE_H_INCLUDED
