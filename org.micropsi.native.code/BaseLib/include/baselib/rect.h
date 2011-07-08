// rect.h
//
// author: David.Salz@snafu.de
// created: April 11, 2004


#ifndef BASELIB_RECT_H_INCLUDED
#define BASELIB_RECT_H_INCLUDED

#include "baselib/macros.h"
#include "baselib/pnt.h"

/*
	Eine einfache Klasse für Rechtecke; kompatibel zu Windows-RECT-Strukur
*/
class CRct
{
public:
	int left, top, right, bottom;		//  "unkonventionelle" Namen für evtl. Kompatiblität mit Window
										//  Achtung: right und bottom sind bereits *außerhalb*, d.h. (0,0,5,5) hat Größe 5x5

	/// default ctor
	CRct()
	{ 
		left = top = right = bottom = 0; 
	}



	/// ctor
	CRct(int p_iLeft, int p_iTop, int p_iRight,int p_iBottom)		
	{
		left = p_iLeft;
		bottom = p_iBottom;
		top = p_iTop;
		right = p_iRight;
	}
	


	/// ctor
	CRct(CPnt xPnt1, CPnt xPnt2)
	{
		left   = xPnt1.x;
		top    = xPnt1.y;
		right  = xPnt2.x;
		bottom = xPnt2.y;
	}



	/// copy ctor
	CRct(const CRct& p_krxRect)
	{
		left	= p_krxRect.left;
		bottom	= p_krxRect.bottom;
		top		= p_krxRect.top;
		right	= p_krxRect.right;
	}



	/// Breite des Rechtecks
	int Width() const		
	{
		return right-left;
	}		
	

	
	/// Höhe des Rechtecks
	int Height() const 
	{
		return bottom-top;
	}	
	


	/// überprüft, ob ein Punkt innerhalb des Rechtecks liegt (Achtung: untere rechte Ecke ist *außerhalb* des Rechtecks!) 
	bool Hit(const CPnt& p_krxPnt) const		
	{
		return	((p_krxPnt.x>=left)  &&  (p_krxPnt.x<right)  &&
				(p_krxPnt.y>=top)	 &&	 (p_krxPnt.y<bottom));
	}



	/// überprüft, ob dieses Rechteck ein anderes schneidet (Achtung: untere rechte Ecke ist *außerhalb* des Rechtecks!)
	bool Intersects(const CRct& p_krxRect) const
	{
		return (left < p_krxRect.right   &&  right  > p_krxRect.left  &&
				top  < p_krxRect.bottom  &&  bottom > p_krxRect.top);
	}


	// überprüft, ob dieses Rechteck das übergebene vollständig enthält
	bool Contains(const CRct& p_krxRect) const
	{
		return 	(p_krxRect.top  >= top)  &&  (p_krxRect.bottom <= bottom) &&
				(p_krxRect.left >= left) &&  (p_krxRect.right <= right);
	}


	///	Liefert den Schnitt der beiden Rechtecke, 
	CRct Clip(const CRct& p_krxRect) const	
	{
		if(!Intersects(p_krxRect))
		{
			return CRct(0,0,0,0);
		};

		return CRct(
			max(left, p_krxRect.left),
			max(top, p_krxRect.top),
			min(right, p_krxRect.right),
			min(bottom, p_krxRect.bottom));
	}



	/// liefert true, wenn dieses Rechteck leer ist (d.h. Höhe == Breite == 0)
    bool IsEmpty() const						
	{	
		return Height() <= 0  ||  Width() <= 0;
	}



	///	liefert das kleinste Bounding-Rechteck
	CRct Bounding(const CRct& p_krxRect) const		
	{
		return CRct(
			min(left, p_krxRect.left),
			min(top, p_krxRect.top),
			max(right, p_krxRect.right),
			max(bottom, p_krxRect.bottom));
	}
	

	
	/// Zuweisung
	CRct& operator=(const CRct& p_krxRect)	
	{
		left = p_krxRect.left;
		bottom = p_krxRect.bottom;
		top = p_krxRect.top;
		right = p_krxRect.right;
		return *this;
	}


	/// Vergleich
	bool operator==(const CRct& p_krxRect) const	
	{
		return ((left==p_krxRect.left) &&
				(bottom==p_krxRect.bottom) &&
				(top==p_krxRect.top) &&
				(right==p_krxRect.right));
	}



	/// Vergleich "ungleich"
	bool operator!=(const CRct& p_krxRect) const	
	{
		return ((left!=p_krxRect.left) ||
				(bottom!=p_krxRect.bottom) ||
				(top!=p_krxRect.top) ||
				(right!=p_krxRect.right));
	}



	/// return rect shifted down/right by amount given through pnt
	CRct operator+(const CPnt& p_krxPnt) const
	{
		return CRct(left+p_krxPnt.x, top+p_krxPnt.y, right+p_krxPnt.x, bottom+p_krxPnt.y);
	}



	/// shift rect down/right by amount given through pnt
	CRct& operator+=(const CPnt& p_krxPnt)
	{
		left+=p_krxPnt.x; top+=p_krxPnt.y; right+=p_krxPnt.x; bottom+=p_krxPnt.y; return *this;
	}



	/// return rect shifted up/left by amount given through pnt
	CRct operator-(const CPnt& p_krxPnt) const
	{
		return CRct(left-p_krxPnt.x, top-p_krxPnt.y, right-p_krxPnt.x, bottom-p_krxPnt.y);
	}



	/// shift rect up/left by amount given through pnt
	CRct& operator-=(const CPnt& p_krxPnt)		
	{
		left-=p_krxPnt.x; top-=p_krxPnt.y; right-=p_krxPnt.x; bottom-=p_krxPnt.y; return *this;
	}


	/// schneidet eine Linie am Rechteck; die Endpunkte werden aktualisiert, damit die Linie vollständig im Rechteck liegt
	/// liefert false, wenn die Linie das Rechteck nicht schneidet (Punkte in diesem Fall unverändert)
	bool ClipLine(CPnt& p_xrA, CPnt& p_xrB) const;


protected:

	/// liefert eine Bitmaske für einen Punkt; Bitmaske zeigt an, wie Punkt zu Rechteck liegt
	unsigned short CRct::ClipLineBitmask(const CPnt& p_krxPnt) const;

	friend class CRctList;
};



#endif // BASELIB_RECT_H_INCLUDED

