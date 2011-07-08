#include "stdafx.h"

#include "baselib/rect.h"


/** liefert eine Bitmaske für einen Punkt
	die 4 Bits stehen für die 4 Begrenzungslinien des Rechtecks, eine 1 zeigt an, dass 
	der Punkt außerhalb der Linie liegt 
	d.h. maske = 0 bedeutet, dass der Punkt im Rechteck liegt
*/
unsigned short CRct::ClipLineBitmask(const CPnt& p_krxPnt) const
{
	unsigned short iBitmask = 0;
	if(p_krxPnt.y <  top)	 { iBitmask |=1 << 0; }		// top
	if(p_krxPnt.y >= bottom) { iBitmask |=1 << 1; }		// bottom
	if(p_krxPnt.x <  left)	 { iBitmask |=1 << 2; }		// left
	if(p_krxPnt.x >= right)	 { iBitmask |=1 << 3; }		// right
	return iBitmask;
}


/**
	schneidet eine Linie am Rechteck; die Endpunkte werden aktualisiert, damit die Linie vollständig im Rechteck liegt
	liefert false, wenn die Linie das Rechteck nicht schneidet (Punkte in diesem Fall unverändert)
*/
bool CRct::ClipLine(CPnt& p_xrA, CPnt& p_xrB) const
{
	CPnt xA = p_xrA;			// Kopien der Punkte machen; falls die Linie am Ende nicht schneidet,
	CPnt xB = p_xrB;			// wollen wir die Punkte nicht kaputtmachen

	unsigned short dwMaskA = ClipLineBitmask(xA);
	unsigned short dwMaskB = ClipLineBitmask(xB);	

	bool bIntersects = false;
	do
	{
		if(dwMaskA == 0 && dwMaskB == 0) 
		{
			// beide Punkte im Rechteck --> fertig, Linie ist komplett drin
			bIntersects = true; 
			break;
		}
		else if(dwMaskA&dwMaskB) 
		{
			// beide Punkte im gleichen Sektor außerhalb --> fertig, Linie schneidet gar nicht
			bIntersects = false; 
			break;
		}	
		else
		{
			// einen der beiden Punkte herauspicken, der nicht im Rechteck liegt
			// eine der beiden Koordinaten des Punktes so korrigieren, dass sie im Rechteck liegt

			unsigned short dwMask = dwMaskA != 0 ? dwMaskA : dwMaskB;
			CPnt p;

			if(dwMask & 1<<0)		// top 
			{
				p.x = xA.x + (xB.x - xA.x) * (top - xA.y) / (xB.y - xA.y);
				p.y = top;
			}
			else if(dwMask & 1<<1)	// bottom
			{
				p.x = xA.x + (xB.x - xA.x) * (bottom -1 - xA.y) / (xB.y - xA.y);
				p.y = bottom - 1;
			}
			else if(dwMask & 1<<2)	// left
			{
				p.y = xA.y + (xB.y - xA.y) * (left - xA.x) / (xB.x - xA.x);
				p.x = left;
			}
			else					// right
			{
				p.y = xA.y + (xB.y - xA.y) * (right - 1 - xA.x) / (xB.x - xA.x);
				p.x = right - 1;
			}

			// Punkt zurückschreiben, mal sehen, ob jetzt eine Entscheidung fallen kann... 
			if(dwMask == dwMaskA)
			{
				xA = p;
				dwMaskA = ClipLineBitmask(xA);
			}
			else
			{
				xB = p;
				dwMaskB = ClipLineBitmask(xB);
			}
		}		
	} while(true);

	if(bIntersects) 
	{
		p_xrA = xA;
		p_xrB = xB;
	}
	
	return bIntersects;		
}

