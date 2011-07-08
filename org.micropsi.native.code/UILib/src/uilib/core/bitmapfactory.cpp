#include "stdafx.h"
#include "uilib/core/bitmapfactory.h"
#include "uilib/core/bitmap.h"
#include "uilib/core/bitmapdevice.h"
#include "uilib/core/alphabitmapfont.h"
#include "baselib/color.h"
#include "baselib/debugprint.h"

// benutzt die DevIL-Lib
#include "il/il.h"


using std::map;
using std::string;

namespace UILib
{

CBitmapFactory* CBitmapFactory::ms_pxInst;
int CBitmapFactory::ms_iNonameCount = 0;


//---------------------------------------------------------------------------------------------------------------------
///	default constructor
CBitmapFactory::CBitmapFactory()
{
	ilInit();
	ilEnable(IL_ORIGIN_SET);
	ilOriginFunc(IL_ORIGIN_UPPER_LEFT);
	AddSearchPath("./");
	m_hFont = 0;
}


//---------------------------------------------------------------------------------------------------------------------
///	default destructor
CBitmapFactory::~CBitmapFactory()
{
	if(m_hFont != 0)
	{
		CAlphaBitmapFont::Release(m_hFont);
	}

	map<string, CBitmapEntry*>::iterator cur;
	cur = m_xFilenameMap.begin();
	while(cur != m_xFilenameMap.end())
	{
		delete cur->second;
		cur->second = 0;
		cur++;
	}
}


//----------------------------------------------------------------------------------------------------------------------
CBitmapFactory& CBitmapFactory::Get()							
{ 
    if(!ms_pxInst)
    {
        ms_pxInst = new CBitmapFactory();
    }
    return *ms_pxInst; 
}
	
//----------------------------------------------------------------------------------------------------------------------
void CBitmapFactory::Shut()
{
    if(ms_pxInst)
    {
        delete ms_pxInst;
        ms_pxInst = 0;
    }
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	registriert eine neu angelegte Bitmap und versorgt die Bitmap mit den 
	eigentlichen Bilddaten. Es gibt zwei Möglichkeiten
	a) die Bitmap ist schon geladen, dann können die Daten direkt verwendet werden
	   d.h. zwei oder mehr Bitmaps teilen sich die Daten
    b) die Bitmap ist noch nicht geladen, dann wird das hier getan; diese Funktion
	   kann mittels libpng PNG-Dateien laden
*/
void 
CBitmapFactory::RegisterBitmap(const CStr& p_rsFilename, CBitmap& po_xrBitmap)
{
	CBitmapEntry* pxEntry = 0;
	map<string, CBitmapEntry*>::iterator cur;
	cur = m_xFilenameMap.find(p_rsFilename.c_str());
	if(cur != m_xFilenameMap.end())
	{
		/// gefunden!
		pxEntry = cur->second;
	}
	else
	{		
		// nicht gefunden, Bitmap laden
	
		po_xrBitmap.m_pxData = 0;

		ILuint ImageName = 0;		// The image name to return.
		ilGenImages(1, &ImageName); // Grab a new image name.
		ilBindImage(ImageName);

		bool bLoaded = false;
		unsigned int iSearchPath = 0;
		while(!bLoaded  &&  iSearchPath < m_asSearchPaths.Size())
		{
			bLoaded = ilLoadImage((ILstring) (m_asSearchPaths[iSearchPath] + p_rsFilename).c_str()) == IL_TRUE;
			iSearchPath++; 
		} 
		
		if(bLoaded)
		{
			ILuint Width, Height;
			Width = ilGetInteger(IL_IMAGE_WIDTH);
			Height = ilGetInteger(IL_IMAGE_HEIGHT); 

			pxEntry = new CBitmapEntry(Width, Height, p_rsFilename);
			ilCopyPixels(0, 0, 0, Width, Height, 1, IL_RGBA, IL_UNSIGNED_BYTE, pxEntry->m_xData.m_puiPixels);
			for(unsigned int i=0; i<Width*Height; ++i)
			{
				CColor xColor = CColor(pxEntry->m_xData.m_puiPixels[i]);
				CColor xFinalColor;
				xFinalColor.m_cRed    = xColor.m_cBlue;
				xFinalColor.m_cGreen  = xColor.m_cGreen;
				xFinalColor.m_cBlue   = xColor.m_cRed;
				xFinalColor.m_cAlpha  = xColor.m_cAlpha;
				pxEntry->m_xData.m_puiPixels[i] = xFinalColor.m_dwColor;
			}
		}
		else
		{
			for(unsigned int i=0; i<m_asSearchPaths.Size(); ++i)
			{
				DebugPrint ("Warning: failed to load: %s", (m_asSearchPaths[i] + p_rsFilename).c_str());
			} 

			int iH = 100;
			int iW = 100;
			pxEntry = new CBitmapEntry(iW, iH, p_rsFilename);
			memset(pxEntry->m_xData.m_puiPixels, 0xFF, iH * iW * 4);
			CBitmapDevice* pxBmpDev = new CBitmapDevice(pxEntry->m_xData.m_puiPixels, iW, iH, 0);
			pxBmpDev->BeginPaint();

			pxBmpDev->FillRect(0, 0, iW, iH, CColor(200, 100, 100, 255));
			if(m_hFont == 0)
			{
				m_hFont = CAlphaBitmapFont::Create(16, "", CFont::L_STANDARD, CFont::PITCH_VARIABLE, CFont::W_NORMAL, 0);
			}
			pxBmpDev->DrawTextRect(m_hFont, CRct(0, 0, iW, iH), COutputDevice::TA_Left, COutputDevice::TA_Top, p_rsFilename, CColor(0, 0, 0));

			pxBmpDev->EndPaint();

            delete pxBmpDev;
		}

		m_xFilenameMap[p_rsFilename.c_str()] = pxEntry;
		ilDeleteImages(1, &ImageName);
	}

	pxEntry->m_iRefCount++;
	po_xrBitmap.m_pxData = &pxEntry->m_xData;
}


//---------------------------------------------------------------------------------------------------------------------
///	erzeugt eine neue Bitmap der gewünschten Größe
void 
CBitmapFactory::RegisterBitmap(int p_iWidth, int p_iHeight, CBitmap& po_xrBitmap)
{
	CStr sFilename = CStr::Create("noname%d", ms_iNonameCount);
	ms_iNonameCount++;
	CBitmapEntry* pxEntry = new CBitmapEntry(p_iWidth, p_iHeight, sFilename);
	m_xFilenameMap[sFilename.c_str()] = pxEntry;
	pxEntry->m_iRefCount++;
	po_xrBitmap.m_pxData = &pxEntry->m_xData;
}


//---------------------------------------------------------------------------------------------------------------------
/// gibt Referenz auf Bitmap wieder frei
void 
CBitmapFactory::UnregisterBitmap(const CBitmap& p_rxBitmap)
{
	assert(p_rxBitmap.m_pxData);
	if(!p_rxBitmap.m_pxData) { return; }

	assert(!m_xFilenameMap.empty());			// keine Bitmaps da... sollte nicht sein!
	if(!m_xFilenameMap.empty()) { return; }

	map<string, CBitmapEntry*>::iterator cur;
	cur = m_xFilenameMap.find(p_rxBitmap.m_pxData->m_sFilename.c_str());

	assert(cur != m_xFilenameMap.end());		// freizugebene Bitmap nicht gefunden... das sollte nie passieren!
	if(cur == m_xFilenameMap.end()) { return; }

	CBitmapEntry* pxEntry = cur->second;
	pxEntry->m_iRefCount--;
	if(pxEntry->m_iRefCount == 0)
	{
		delete pxEntry;
		m_xFilenameMap.erase(cur);
	}
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Stellt sicher, dass die übergebene Bitmap ihre Daten nicht mit einer
	anderen Bitmap teil; das ist notwendig, wenn in die Bitmap gezeichnet
	werden soll.
	Falls notwendig, wird eine Kopie der Daten Bitmap gemacht.
*/
bool 
CBitmapFactory::MakeUnshared(CBitmap* po_pxBitmap)
{
	assert(po_pxBitmap->m_pxData);
	if(!po_pxBitmap->m_pxData) { return false; }

	map<string, CBitmapEntry*>::iterator cur;
	cur = m_xFilenameMap.find(po_pxBitmap->m_pxData->m_sFilename.c_str());
	assert(cur != m_xFilenameMap.end());		// Bitmap nicht gefunden... 
	if(cur == m_xFilenameMap.end()) { return false; }

	CBitmapEntry* pxEntry = cur->second;
    if(pxEntry->m_iRefCount == 1)
	{
		// die Daten werden ohnehin nur von dieser einen Bitmap genutzt;
		// es reicht daher, einen neuen Namen zu vergeben

		CStr sFilename = CStr::Create("noname%d", ms_iNonameCount);
		ms_iNonameCount++;
		m_xFilenameMap[sFilename.c_str()] = pxEntry;
		m_xFilenameMap.erase(cur);
		pxEntry->m_xData.m_sFilename = sFilename;
		return true;
	}
	else
	{
		// Daten müssen kopiert werden

		pxEntry->m_iRefCount--;

		CStr sFilename = CStr::Create("noname%d", ms_iNonameCount);
		ms_iNonameCount++;

		CBitmapEntry* pxNewEntry = new CBitmapEntry(pxEntry->m_xData.m_xSize.cx, pxEntry->m_xData.m_xSize.cy, sFilename);
		memcpy(pxNewEntry->m_xData.m_puiPixels, pxEntry->m_xData.m_puiPixels, sizeof(unsigned long)*pxEntry->m_xData.m_iNumPixels);

		m_xFilenameMap[sFilename.c_str()] = pxNewEntry;
		pxNewEntry->m_iRefCount++;
		po_pxBitmap->m_pxData = &(pxNewEntry->m_xData);

		return true;
	}
}


//---------------------------------------------------------------------------------------------------------------------
void 
CBitmapFactory::AddSearchPath(const CStr& p_rsPath)
{
	CStr sPath = p_rsPath;
	if(!sPath.IsEmpty())
	{
		sPath.Replace('\\', '/');
		if(sPath.GetAt(sPath.GetLength()-1) != '/')
		{
			sPath += '/';
		}
		m_asSearchPaths.PushEntry(sPath);
	}
}


//---------------------------------------------------------------------------------------------------------------------
CBitmapFactory::CBitmapEntry::CBitmapEntry(int p_iWidth, int p_iHeight, const CStr& p_rsFilename)
{
	m_iRefCount = 0;
	assert(p_iWidth >= 0 && p_iHeight >= 0);
	p_iWidth = max(0,p_iWidth);
	p_iHeight = max(0,p_iHeight);
	m_xData.m_xSize = CSize(p_iWidth, p_iHeight);
	m_xData.m_iNumPixels = p_iWidth * p_iHeight;
	m_xData.m_sFilename = p_rsFilename;
	if(m_xData.m_iNumPixels > 0)
	{
		m_xData.m_puiPixels = new unsigned long [m_xData.m_iNumPixels];
	}
	else
	{
		m_xData.m_puiPixels = 0;
	}
}


//---------------------------------------------------------------------------------------------------------------------
CBitmapFactory::CBitmapEntry::~CBitmapEntry()
{
	if(m_xData.m_puiPixels)
	{
		delete m_xData.m_puiPixels;
	}
}

} // namespace UILib

