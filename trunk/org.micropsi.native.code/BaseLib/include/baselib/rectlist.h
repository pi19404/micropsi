#ifndef BASELIB_RECTLIST_H_INCLUDED
#define BASELIB_RECTLIST_H_INCLUDED

#include "baselib/rect.h"
#include "baselib/pnt.h"
#include "baselib/dynarray.h"

/**
	Liste von Rechtecken, kann Liste disjunkt halten; d.h. so, dass sich keine Rechtecke der Liste �berlappen
	und die Gesamtfl�che sich trotzdem nicht ver�ndert
	Die Rechtecke in der Liste haben keine besondere Reihenfolge
*/
class CRctList
{
public:

	/// Liste komplett l�schen
	void Clear()	{ m_axList.Clear(); }				

	/// h�ngt Rechteck an Liste an; bleibt *nicht* disjunkt
	void Push(CRct p_xRect);							

	/// h�ngt Rechteck an Liste an; bleibt *nicht* disjunkt
	void Push(int p_iLeft, int p_iTop, int p_iRight, int p_iBottom);	

	/// h�ngt Liste an Liste an; bleibt *nicht* disjunkt
	void Push(const CRctList& p_krxOther);

	/// entfernt letztes Rechteck aus der Liste
	bool Pop(CRct& po_xrRect);

	/// f�ge neues Rechteck in die Liste ein; Liste bleibt disjunkt
	void Add(const CRct& p_krxRect);

	/// f�ge Liste von Rechtecken in die Liste ein; Liste bleibt disjunkt
	void Add(const CRctList& p_axrList);

	/// l�scht (beliebiges) Rechteck aus der Gesamtfl�che; Liste bleibt disjunkt
	void Sub(const CRct& p_krxRect);								

	/// l�scht (beliebige) Liste von Rechtecken aus der Gesamtfl�che; Liste bleibt disjunkt
	void Sub(const CRctList& p_axrList);

	/// Schneidet die Gesamtfl�che an einem Rechteck; Liste bleibt disjunkt 
	void Clip(const CRct& p_krxClipRect);

	/// liefert true, wenn das �bergebene Rechteck in der Liste vollst�ndig enthalten ist
	bool Contains(const CRct& p_rxRect);

	///< Optimiert die Liste; d.h. versucht die Listengr��e zu reduzieren ohne die Gesamtfl�che zu ver�ndern
	void Compact();										

	void StartIterate(unsigned int& iIterator) const;			
	bool Iterate(unsigned int& iIterator,CRct& rTarget) const;

	/// liefert kleinstes Bounding-Rechteck der Liste
	CRct GetBoundingRect() const;

	/// testet, ob Punkt in Liste liegt
	bool PointHitTest(const CPnt& p_krxPnt) const;

	/// liefert Gr��e der Liste
	unsigned int	 Size() const			{ return m_axList.Size(); }

	/// liefert true, wenn die Liste leer ist
	bool			IsEmpty() const			{ return m_axList.Size() == 0; }

	/// �berpr�ft, ob die Elemente der Menge disjunkt sind
	bool			IsDisjunct() const;

protected:

	CDynArray<CRct, 5, false> m_axList;					///< die eigentliche Liste (unsortiert)
};


#endif // ifndef BASELIB_RECTLIST_H_INCLUDED

