#include "stdafx.h"
#include <limits.h>

#include "baselib/rectlist.h"

//---------------------------------------------------------------------------------------------------------------------
bool 
CRctList::PointHitTest(const CPnt& p_krxPnt) const
{
	for(unsigned int i=0; i<m_axList.Size(); i++)
	{
		if(m_axList[i].Hit(p_krxPnt))
		{
			return true;
		}
	}
	return false;
}

//---------------------------------------------------------------------------------------------------------------------
void 
CRctList::Push(CRct p_xRect)
{
	if(p_xRect.IsEmpty())
	{
		return; 
	}
	m_axList.PushEntry(p_xRect);
}

//---------------------------------------------------------------------------------------------------------------------
void 
CRctList::Push(int p_iLeft, int p_iTop, int p_iRight, int p_iBottom)
{
	if(p_iBottom - p_iTop  <= 0) { return; }
	if(p_iRight  - p_iLeft <= 0) { return; }

	unsigned int i = m_axList.PushEntry();
	m_axList[i].top    = p_iTop;
	m_axList[i].right  = p_iRight;
	m_axList[i].left   = p_iLeft;
	m_axList[i].bottom = p_iBottom;
}


//---------------------------------------------------------------------------------------------------------------------
void 
CRctList::Push(const CRctList& p_krxOther)
{
	for(unsigned int i=0; i<p_krxOther.m_axList.Size(); ++i)
	{
		Push(p_krxOther.m_axList[i]);
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CRctList::Pop(CRct& po_xrRect)
{
	unsigned int i = m_axList.Size();
	if(i == 0)	{ return false; }
	po_xrRect = m_axList[i-1];
	m_axList.PopEntry();
	return true;
}

//---------------------------------------------------------------------------------------------------------------------
void 
CRctList::Add(const CRct& p_krxRect)
{
	if(p_krxRect.IsEmpty()) { return; }

	if(m_axList.Size() == 0)
	{
		m_axList.PushEntry(p_krxRect);
		return;
	}

	CRct xCurToDo, xCurRect;
	CRctList axToDo;

	// beim Einfügen eines Rechtecks in die Liste gibt es drei mögliche Fälle:
	//   1. Das Rechteck ist völlig disjunkt von allen Listenrechtecken --> Rechteck an die Liste anhängen
	//   2. Das Rechteck ist in *einem* der Listenrechtecke vollständig enthalten --> Rechteck ignorieren
	//   3. Das Rechteck schneidet ein Listenrechteck, ist aber nicht vollständig in diesem enthalten
	//      --> in diesem Fall bilden wir die Teilrechtecke, die beim Schnitt mit dem Listenrechteck übrig bleiben,
	//		das sind bis zu vier. Für diese Teile wiederholen wir den ganzen Algorithmus jeweils.
	// 
	// die Liste axToDo ist die Liste der Rechtecke, die noch zur Liste hinzugefügt (und vorher überprüft) werden müssen.
	// Anfangs steht in axToDo nur das übergebene Rechteck, später kommen aus Fall 3 evtl. weitere Rechtecke dazu
	// wir sind fertig, wenn axToDo leer ist

	axToDo.Push(p_krxRect);
	while(axToDo.Pop(xCurToDo))
	{
		bool bAddIt=true;
		for(unsigned int i=0; i<m_axList.Size(); ++i)
		{
			xCurRect = m_axList[i];
			if(xCurRect.Intersects(xCurToDo))
			{
				bAddIt=false;
				if(!xCurRect.Contains(xCurToDo))
				{
					axToDo.Push(xCurToDo.left, xCurToDo.top,    xCurToDo.right, xCurRect.top);
					axToDo.Push(xCurToDo.left, xCurRect.bottom, xCurToDo.right, xCurToDo.bottom);
					int t,b;
					t=max(xCurToDo.top, xCurRect.top);
					b=min(xCurToDo.bottom, xCurRect.bottom);
					axToDo.Push(xCurToDo.left,  t, xCurRect.left,  b);
					axToDo.Push(xCurRect.right, t, xCurToDo.right, b);
					break;
				}
			}
		}

		if(bAddIt)
		{
			m_axList.PushEntry(xCurToDo);
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
CRctList::Add(const CRctList& p_kaxrOther)
{
	for(unsigned int i=0; i<p_kaxrOther.Size(); ++i)
	{
		Add(p_kaxrOther.m_axList[i]);
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
CRctList::Sub(const CRct& p_krxRect)
{
	if(p_krxRect.IsEmpty())		{ return; }

	// Algorithmus: wir bauen eine neue Liste auf. 
	// jedes Rechteck der alten Liste, welches das zu subtrahierende nicht schneidet, ist in der neuen Liste.
	// Ansonsten muss es in Teile gespalten werden. Die Teile, die das zu subtrahierende Rechteck nicht spalten,
	// kommen in die neue Liste

	CRctList axNewList;
	CRct r;

	unsigned int i;
	for(i=0; i<m_axList.Size(); ++i)
	{
		if(!p_krxRect.Intersects(m_axList[i]))
		{
			axNewList.Add(m_axList[i]);
		}
		else
		{
			axNewList.Push(m_axList[i].left, m_axList[i].top,  m_axList[i].right, p_krxRect.top);
			axNewList.Push(m_axList[i].left, p_krxRect.bottom, m_axList[i].right, m_axList[i].bottom);
			int t,b;
			t = max(m_axList[i].top,    p_krxRect.top);
			b = min(m_axList[i].bottom, p_krxRect.bottom);
			axNewList.Push(m_axList[i].left, t, p_krxRect.left,    b);
			axNewList.Push(p_krxRect.right,  t, m_axList[i].right, b);
		}
	}
	Clear();
	m_axList.SetSize(axNewList.m_axList.Size());
	for(i=0; i<axNewList.Size(); ++i)
	{
		m_axList[i] = axNewList.m_axList[i];
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
CRctList::Sub(const CRctList& p_kaxrOther)
{
	for(unsigned int i=0; i<p_kaxrOther.Size(); ++i)
	{
		Sub(p_kaxrOther.m_axList[i]);
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
CRctList::Clip(const CRct& p_krxClipRect)
{
	if(p_krxClipRect.IsEmpty())
	{
		Clear();
		return;
	}

	// Algorithmus: wir bauen eine neue Liste auf. Jedes Rechteck wird mit dem Clipping-Rect geschnitten. Der Schnitt
	// kommt in die neue Liste (falls er nicht leer ist)

	CRctList axNewList;
	CRct r;
	unsigned int i;
	for(i=0; i<m_axList.Size(); i++)
	{
		r.left   = max(p_krxClipRect.left,   m_axList[i].left);
		r.right  = min(p_krxClipRect.right,  m_axList[i].right);
		r.top    = max(p_krxClipRect.top,    m_axList[i].top);
		r.bottom = min(p_krxClipRect.bottom, m_axList[i].bottom);
		axNewList.Push(r);
	}
	Clear();
	m_axList.SetSize(axNewList.m_axList.Size());
	for(i=0; i<axNewList.m_axList.Size(); i++)
	{
		m_axList[i] = axNewList.m_axList[i];
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CRctList::Contains(const CRct& p_rxRect)
{
	CRctList xRemainingParts;
	xRemainingParts.Push(p_rxRect);
	for(unsigned int i=0; i<m_axList.Size(); ++i)
	{
		if(m_axList[i].Intersects(p_rxRect))
		{
			xRemainingParts.Sub(m_axList[i]);
			if(xRemainingParts.IsEmpty())
			{
				return true;
			}
		}
	}

	return false;
}

//---------------------------------------------------------------------------------------------------------------------
void 
CRctList::Compact()
{
	if(m_axList.Size() < 2) { return; }

	// Algorithmus: wir vergleichen paarweise alle Rechtecke. Wenn zwei Rechtecke perfekt aneinanderpassen, werden sie zu einem 
	// einzigen vereinigt. Jede Änderung an der Liste bedeutet natürlich, dass alles noch einmal von vorn verglichen werden muss.
	// Sobald sich nichts mehr ändert, sind wir fertig.

	unsigned int i,k;
	bool bDone = false;
	while(!bDone)
	{
		bDone=true;

		// zwei Rechtecke mit gleicher Höhe direkt nebeneinander?
		for(i=0; i<m_axList.Size(); ++i)
		{
			for(k=0; k<m_axList.Size(); ++k)
			{
				if(i!=k)
				{
					if((m_axList[i].top     == m_axList[k].top)    &&
						(m_axList[i].bottom == m_axList[k].bottom) &&
						(m_axList[i].right  == m_axList[k].left))
					{
						bDone=false;
						m_axList[i].right = m_axList[k].right;
						m_axList.DeleteEntry(k);
						break;
					}
				}
			}
		}

		// zwei Rechtecke mit gleicher Breite direkt übereinander?
		for(i=0; i<m_axList.Size(); ++i)
		{
			for(k=0; k<m_axList.Size(); ++k)
			{
				if(i!=k)
				{
					if((m_axList[i].left    == m_axList[k].left)&&
						(m_axList[i].right  == m_axList[k].right)&&
						(m_axList[i].bottom == m_axList[k].top))
					{
						bDone=false;
						m_axList[i].bottom=m_axList[k].bottom;
						m_axList.DeleteEntry(k);
						break;
					}
				}
			}
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
CRctList::StartIterate(unsigned int& po_irIterator) const
{
	po_irIterator=0;
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CRctList::Iterate(unsigned int& po_irIterator,CRct& po_xrTarget) const
{
	if(po_irIterator >= m_axList.Size()) { return false; }
	po_xrTarget=m_axList[po_irIterator];
	po_irIterator++;
	return true;
}

//---------------------------------------------------------------------------------------------------------------------
CRct 
CRctList::GetBoundingRect() const
{
	CRct rct;
	rct.top   = INT_MAX; rct.left   = INT_MAX;
	rct.right = INT_MIN; rct.bottom = INT_MIN;
	for(unsigned int i=0; i<m_axList.Size(); ++i)
	{
		rct.top    = min(rct.top,    m_axList[i].top);
		rct.bottom = max(rct.bottom, m_axList[i].bottom);
		rct.left   = min(rct.left,   m_axList[i].left);
		rct.right  = max(rct.right,  m_axList[i].right);
	}	
	return rct;
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CRctList::IsDisjunct() const
{
	if(m_axList.Size() < 2) 
	{
		return true;
	}

	for(unsigned int i=0; i<m_axList.Size()-1; ++i)
	{
		for(unsigned int j=i+1; j<m_axList.Size(); ++j)
		{
			if(m_axList[i].Intersects(m_axList[j]))
			{
				return false;
			}
		}
	}
	return true;
}
//---------------------------------------------------------------------------------------------------------------------

