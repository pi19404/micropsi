#include "stdafx.h"
#include <assert.h>
#include "uilib/core/bitmap.h"
#include "uilib/core/bitmapfactory.h"

namespace UILib
{

int CBitmap::ms_iBitmaps = 0;


//---------------------------------------------------------------------------------------------------------------------
/**
	Konstruktor
*/
CBitmap::CBitmap()
{
	m_pxData=0;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Konstruktor aus Datei
*/
CBitmap::CBitmap(CStr p_sFilename)
{
	CBitmapFactory::Get().RegisterBitmap(p_sFilename, *this);

	ms_iBitmaps++;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Konstruktor: neue, leer Bitmap
*/
CBitmap::CBitmap(int p_iWidth, int p_iHeight, bool p_bClear)
{
	CBitmapFactory::Get().RegisterBitmap(p_iWidth, p_iHeight, *this);
	if(p_bClear)
	{
		memset(GetRawDataForWriting(), 0, p_iWidth * p_iHeight * 4);
	}

	ms_iBitmaps++;
}

//---------------------------------------------------------------------------------------------------------------------
/**
	Destruktor
*/
CBitmap::~CBitmap()
{
	CBitmapFactory::Get().UnregisterBitmap(*this);
	m_pxData=0;
	ms_iBitmaps--;
}


//---------------------------------------------------------------------------------------------------------------------
/// get size of bitmap
CSize CBitmap::GetSize() const
{
	assert(m_pxData);
	return m_pxData->m_xSize;
}


//---------------------------------------------------------------------------------------------------------------------
/// get bitmap height						
int CBitmap::GetHeight() const
{
	assert(m_pxData);
	return m_pxData->m_xSize.cy;
}


//---------------------------------------------------------------------------------------------------------------------
/// get bitmap width
int CBitmap::GetWidth() const
{
	assert(m_pxData);
	return m_pxData->m_xSize.cx;
}


//---------------------------------------------------------------------------------------------------------------------
/// get number of pixels
int CBitmap::GetNumPixels() const
{
	assert(m_pxData);
	return m_pxData->m_iNumPixels;
}


//---------------------------------------------------------------------------------------------------------------------
/// get pointer to raw pixel data
const unsigned long* CBitmap::GetRawData() const
{
	assert(m_pxData);
	return m_pxData->m_puiPixels;
}


//---------------------------------------------------------------------------------------------------------------------
/// liefert Pointer auf Daten, die zum Schreiben genutzt werden können
unsigned long* CBitmap::GetRawDataForWriting()
{
	bool bRes = CBitmapFactory::Get().MakeUnshared(this);
	assert(bRes);
	if(!bRes)	{return 0;}

	assert(m_pxData);
	if(!m_pxData)	{return 0;}

	return m_pxData->m_puiPixels;
}

//---------------------------------------------------------------------------------------------------------------------
/// liefert Dateinamen, mit dem diese Bitmap ursprünglich erzeugt worden ist
const CStr&	CBitmap::GetFilename() const
{
	assert(m_pxData);
	return m_pxData->m_sFilename; 
}


} // namespace UILib
