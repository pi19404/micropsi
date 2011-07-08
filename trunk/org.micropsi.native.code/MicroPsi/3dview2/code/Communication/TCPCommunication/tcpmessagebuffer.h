// messagebuffer.h
//
// author: David.Salz@snafu.de
// created: June 15, 2003 


#ifndef MESSAGEBUFFER_H_INCLUDED
#define MESSAGEBUFFER_H_INCLUDED

class CInMessageBuffer 
{
public:
	CInMessageBuffer(int p_iSize);
	virtual ~CInMessageBuffer();

	char*		GetBuffer()				{ return m_pxBuffer; };
	int			GetBufferSize() const	{ return m_iBufferSize; };

	void		ResetReadPos()			{ m_iReadPos = 0; };

	bool		ReadChar(char& po_crChar);
	bool		ReadWord(int& po_irInt);
	bool		ReadInt(int& po_irInt);
	bool		ReadLong(long& po_irInt);
	bool		ReadFloat(float& po_frFloat);
	int			ReadStringSize() const;
	bool		ReadString(char* po_pcString, int p_iOutBufferSize);


private:

	char*		m_pxBuffer;
	int			m_iBufferSize; 
	int			m_iReadPos;
};

#endif // MESSAGEBUFFER_H_INCLUDED
