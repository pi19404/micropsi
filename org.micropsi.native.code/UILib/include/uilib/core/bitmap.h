#ifndef UILIB_BITMAP_H_INCLUDED
#define UILIB_BITMAP_H_INCLUDED

#include "baselib/size.h"
#include "baselib/str.h"
#include "bitmapfactory.h"

namespace UILib
{

class CBitmap
{
public:
	CBitmap();
	CBitmap(CStr p_sFilename);
	CBitmap(int p_iWidth, int p_iHeight, bool p_bClear = true);
	virtual ~CBitmap();

	CSize	GetSize() const;
	int		GetHeight() const;
	int		GetWidth() const;
	int		GetNumPixels() const;

	const unsigned long*	GetRawData() const;
	unsigned long*			GetRawDataForWriting();

	const CStr&				GetFilename() const;

protected:

	CBitmapFactory::CBitmapData*	m_pxData;			///< Zeiger auf Bitmap-Daten
	static int						ms_iBitmaps;		///< globaler Bitmap-Zähler; eigentlich nur für Debug-Zwecke

	friend class CBitmapFactory;

private:
	CBitmap(const CBitmap&);
	CBitmap& operator=(const CBitmap&);
};


} // namespace UILib

#endif  // ifndef UILIB_BITMAP_H_INCLUDED
