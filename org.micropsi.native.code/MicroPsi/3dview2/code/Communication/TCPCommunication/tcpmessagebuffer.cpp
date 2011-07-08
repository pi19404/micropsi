
#include <string.h>
#include "Communication/TCPCommunication/tcpmessagebuffer.h"

//---------------------------------------------------------------------------------------------------------------------
CInMessageBuffer::CInMessageBuffer(int p_iSize)
{
	m_pxBuffer = new char[p_iSize];
	m_iBufferSize = p_iSize; 
	m_iReadPos = 0;
}


//---------------------------------------------------------------------------------------------------------------------
CInMessageBuffer::~CInMessageBuffer()
{
	delete m_pxBuffer;
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CInMessageBuffer::ReadChar(char& po_crChar)
{
	if(m_iReadPos > m_iBufferSize -1)	{ return false; }
	po_crChar = m_pxBuffer[m_iReadPos];
	m_iReadPos++;
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CInMessageBuffer::ReadWord(int& po_irInt)
{
	if(m_iReadPos > m_iBufferSize -2)	{ return false; }

	// network byte order: big endian; most significant bytes first
	po_irInt =	(m_pxBuffer[m_iReadPos  ] << 8) &
				(m_pxBuffer[m_iReadPos+1]);
	m_iReadPos+=2;
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CInMessageBuffer::ReadInt(int& po_irInt)
{
	if(m_iReadPos > m_iBufferSize -4)	{ return false; }

	// network byte order: big endian; most significant bytes first
	po_irInt =	((m_pxBuffer[m_iReadPos  ] << 24) & 0xFF000000) +
				((m_pxBuffer[m_iReadPos+1] << 16) & 0x00FF0000) +
				((m_pxBuffer[m_iReadPos+2] << 8)  & 0x0000FF00) +
				( m_pxBuffer[m_iReadPos+3]		  & 0x000000FF);
	m_iReadPos+=4;
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CInMessageBuffer::ReadLong(long& po_irInt)
{
	if(m_iReadPos > m_iBufferSize -8)	{ return false; }

	// network byte order: big endian; most significant bytes first
	int iHi, iLow;
	ReadInt(iHi);
	ReadInt(iLow);
	po_irInt = (((long) iHi) << 32) + iLow;
	
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CInMessageBuffer::ReadFloat(float& po_frFloat)
{
	if(m_iReadPos > m_iBufferSize -4)	{ return false; }
	union 
	{
		int i;
		float f;
	} xValue;
	
	ReadInt(xValue.i);
	po_frFloat = xValue.f;
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	returns length of string at current read position
	string length includes the terminating 0
	the string is not read and the read position is not changed
*/
int 
CInMessageBuffer::ReadStringSize() const
{
	if(m_iReadPos > m_iBufferSize -1)	{ return 0; }
	
	return (int) strlen(&m_pxBuffer[m_iReadPos]) + 1;  
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CInMessageBuffer::ReadString(char* po_pcString, int p_iOutBufferSize)
{
	if(m_iReadPos > m_iBufferSize -1)	{ return false; }
	
	if(po_pcString) 
	{
		strncpy(po_pcString, &m_pxBuffer[m_iReadPos], p_iOutBufferSize);
		int iLength = (int) strlen(&m_pxBuffer[m_iReadPos]) +1;
		if(iLength > p_iOutBufferSize)
		{
			// add terminating 0 because strncpy does not do it
			po_pcString[p_iOutBufferSize-1] = 0;
		}
		m_iReadPos += iLength;
		return true;
	}
	else
	{
		return false;
	}
}
//---------------------------------------------------------------------------------------------------------------------
