#ifndef UILIB_BITMAPFACTORY_H_INCLUDED
#define UILIB_BITMAPFACTORY_H_INCLUDED

#include <map>
#include <string>
#include "baselib/size.h"
#include "baselib/dynarray.h"
#include "font.h"

namespace UILib
{

class CBitmap;

class CBitmapFactory
{
public:
	
	static CBitmapFactory& Get();
    static void Shut();

	void RegisterBitmap(const CStr& p_rsFilename, CBitmap& po_xrBitmap);
	void RegisterBitmap(int p_iWidth, int p_iHeight, CBitmap& po_xrBitmap);

	void UnregisterBitmap(const CBitmap& p_rxBitmap);

	bool MakeUnshared(CBitmap* po_pxBitmap);

	void AddSearchPath(const CStr& p_rsPath);

protected:

	CBitmapFactory();
	~CBitmapFactory();

	static CBitmapFactory*		ms_pxInst;
	static int					ms_iNonameCount;

	CDynArray<CStr>				m_asSearchPaths;	///< Suchpfade f�r Bitmaps
	CFontHandle					m_hFont;			///< font; wird f�r die Erzeugung von Platzhaltern benutzt

	class CBitmapData
	{
	public:
		CSize			m_xSize;			///< Gr��e der Bitmap in Pixeln
		int				m_iNumPixels;		///< Anzahl Pixel
		unsigned long*	m_puiPixels;		///< Zeiger auf eigentliche Daten
		CStr			m_sFilename;		///< Dateiname, mit der diese Bitmap erzeugt wurde
	};


	class CBitmapEntry
	{
	public:
		int					m_iRefCount;		///< reference counter 
		CBitmapData			m_xData;			///< Daten f�r diese Bitmap

		CBitmapEntry(int p_iWidth, int p_iHeight, const CStr& p_rsFilename);
		~CBitmapEntry();
	};

	std::map<std::string, CBitmapEntry*> m_xFilenameMap;	///< Map zwischen Dateinamen und eigentlichen Daten

	friend class CBitmap;

private:
	CBitmapFactory(const CBitmapFactory&);				
	CBitmapFactory& operator=(const CBitmapFactory&);
};

}	//namespace UILib

#endif // ifndef  UILIB_BITMAPFACTORY_H_INCLUDED
