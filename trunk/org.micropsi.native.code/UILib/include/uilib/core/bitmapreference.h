#ifndef UILIB_BITMAPREFERENCE_H_INCLUDED
#define UILIB_BITMAPREFERENCE_H_INCLUDED

namespace UILib
{

/**
	Eine Bitmapreference macht den Umgang mit Bitmaps, insbesondere das Freigeben, einfacher.
	Sie verweist auf eine Bitmap und gibt diese bei ihrer Zerstörung automatisch frei. Wenn zwischenzeitlich eine
	andere Bitmap zugewieden wird, wird die vorherige freigegeben.
*/
class CBitmapRef
{
public:
	CBitmapRef()		{ m_pxBitmap = 0; }
	CBitmapRef(const CStr& p_rsBitmap)
	{
		if(!p_rsBitmap.IsEmpty())
		{
			m_pxBitmap = new CBitmap(p_rsBitmap); 
		}
		else
		{
			m_pxBitmap = 0;
		}
	}

	CBitmapRef(const CBitmapRef& p_rxBitmap)
	{
		if(p_rxBitmap.IsNotEmpty())
		{
			m_pxBitmap = new CBitmap(p_rxBitmap.GetBitmap()->GetFilename()); 
		}
		else
		{
			m_pxBitmap = 0;
		}
	}

	~CBitmapRef()		{ Clear(); }

	CBitmapRef&	operator= (const CBitmap* p_pxBitmap)	
	{ 
		if(m_pxBitmap == 0  ||  m_pxBitmap != p_pxBitmap)
		{
			Clear(); 
			m_pxBitmap = new CBitmap(p_pxBitmap->GetFilename()); 
		}
		return *this;
	}
	
	CBitmapRef&	operator= (const CStr& p_rsBitmap)	
	{ 
		if(m_pxBitmap == 0  ||  m_pxBitmap->GetFilename() != p_rsBitmap)
		{
			Clear(); 
			if(!p_rsBitmap.IsEmpty())
			{
				m_pxBitmap = new CBitmap(p_rsBitmap); 
			}
		}
		return *this;
	}

	bool operator== (const CBitmapRef& p_pxOther) const 	{ return m_pxBitmap == p_pxOther.m_pxBitmap; }
	bool operator!= (const CBitmapRef& p_pxOther) const 	{ return m_pxBitmap != p_pxOther.m_pxBitmap; }
	bool operator== (const CBitmap* p_pxBmp) const 			{ return m_pxBitmap == p_pxBmp; }
	bool operator!= (const CBitmap* p_pxBmp) const 			{ return m_pxBitmap != p_pxBmp; }
	bool operator== (const CStr& p_rsBitmap) const 		{ return m_pxBitmap ? (m_pxBitmap->GetFilename() == p_rsBitmap) : p_rsBitmap == ""; }
	bool operator!= (const CStr& p_rsBitmap) const 		{ return m_pxBitmap ? (m_pxBitmap->GetFilename() != p_rsBitmap) : p_rsBitmap != ""; }

	bool IsEmpty() const				{ return m_pxBitmap == 0; }
	bool IsNotEmpty() const				{ return m_pxBitmap != 0; }
	operator const bool() const			{ return m_pxBitmap != 0; }

	void Clear()						{ if(m_pxBitmap) { delete m_pxBitmap; m_pxBitmap = 0; } }

	operator const CBitmap*() const		{ return m_pxBitmap; }
	const CBitmap* GetBitmap() const	{ return m_pxBitmap; }

private:

	const CBitmap*	m_pxBitmap;					///< Zeiger auf eigentliche Bitmap
};


} // namespace UILib

#endif  // UILIB_BITMAPREFERENCE_H_INCLUDED

