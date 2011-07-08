/*
	Ein Handled Set ist eine Menge von Objekten, �hnlich einem Array. Die einzelnen Elemente des Sets werden 
	aber nicht �ber ihren Index, sondern �ber Handles angesprochen. Beim erzeugen eines neuen Elements im Set 
	erh�lt man ein Handle. Wird das Element aus dem Set entfernt, wird das Handle ung�ltig. Wir sp�ter mit 
	diesem ung�ltigen Handle auf das Set zugegriffen, wird das Handle als ung�ltig erkannt. 
	Die Elemente des Sets haben keine verl��liche Reihenfolge, lassen sich aber iterieren. 

	Die Handles werden nicht 0; 0 kann daher immer als ung�ltiges Handle verwendet werden.
*/

#ifndef BASELIB_HANDLEDSET_H_INCLUDED
#define BASELIB_HANDLEDSET_H_INCLUDED

#include <assert.h>
#include "baselib/macros.h"
#include "baselib/dynarray.h"



template <class _ElementType, unsigned int uiGrowthExp = 5>
class CHandledSet
{
public:
    typedef _ElementType* (__cdecl* CreateElementFunction)();
    typedef void (__cdecl* DestroyElementFunction)(_ElementType* pElement);


	CHandledSet(CreateElementFunction p_fpCreateElement = StdCreateElement,
                DestroyElementFunction p_fpDestroyElement = StdDestroyElement);
	~CHandledSet();

	/// startet eine Iteration des Sets
	void			StartIterate(unsigned long& p_iIterator) const;


	/// Iterationsschritt; liefert true, wenn der Out-Parameter das n�chste Element enth�lt; false bei Ende der Iteration
	bool			Iterate(unsigned long& p_iIterator, _ElementType& po_rxElement) const;

	/// Iterationsschritt; liefert true, wenn der Out-Parameter das n�chste Element enth�lt; false bei Ende der Iteration
	bool			Iterate(unsigned long& p_iIterator, _ElementType*& po_rpxElement) const;

	/// erzeugt einen neuen Eintrag, liefert Handle auf (noch leeren) Eintrag
	unsigned long	PushEntry();


	/// erzeugt neues Handle und weist diesem das �bergebene Element zu
	unsigned long	PushEntry(const _ElementType& p_rxElement);


	/// l�scht einen Eintrag, das Handle wird dabei ung�ltig; liefert true bei Erfolg, false bei ung�ltigem Handle
	bool			DeleteEntry(unsigned long p_uiHandle);


	/// l�scht das gesamte Set (dadurch wird die Zahl Slots nicht kleiner, da die Generation-Info erhalten bleibt!)
	void			Clear();


	/// liefert true, wenn dieses Handle g�ltig ist
	bool			IsValid(unsigned long p_uiHandle) const;


	/// liefet ein Element zur�ck; vorher sollte gepr�ft werden, ob Element Handle g�ltig ist!
	_ElementType&	Element(unsigned long p_uiHandle) const;

	/// liefet ein Element (als Pointer) zur�ck; liefert 0, wenn Handle ung�ltig war
	_ElementType*	ElementPtr(unsigned long p_uiHandle) const;

	/// weist einem Element einen Wert zu; liefet false, wenn Handle ung�ltig war
	bool			SetElement(unsigned long p_uiHandle, const _ElementType& p_krxElement) const;

	/// liefert die Gr��e des Sets (= Anzahl Elemente)
	unsigned int	Size() const;

	/// liefert ein ung�ltiges Handle zur�ck
	static unsigned long InvalidHandle();

private:

    CreateElementFunction     m_fpCreateElement;    ///< Funktion zum Anlegen von Elementen
    DestroyElementFunction    m_fpDestroyElement;   ///< Funktion zum L�schen von Elementen

    static _ElementType* __cdecl StdCreateElement();                ///< Default-Create-Funktion
    static void __cdecl StdDestroyElement(_ElementType* pElement);  ///< Default-L�sch-Funktion


	class CElement
	{
	public:
		CElement()			{ m_pxElementPtr = 0;	m_uiGeneration = 1; }

		_ElementType*		m_pxElementPtr;			///< Zeiger auf eigentliches Element; ist 0, wenn der Slot leer ist
		unsigned short		m_uiGeneration;			///< Generationsz�hler
	};
	
	union THandle
	{
		struct 
		{
			unsigned short		m_uiIndex;
			unsigned short		m_uiGeneration;
		};
		unsigned long			m_iHandle;
	};


    int             GetFreeSlotIdx();                           ///< liefert einen freien slot zur�ck


	CDynArray<CElement, uiGrowthExp, true>	m_axElements;		///< Array mit Elementen
	CDynArray<int, uiGrowthExp, false>		m_aiEmptySlots;		///< Array mit Indizes von leeren (=wiederverwendbaren) Slots
};


#include "baselib/handledset.inl"

#endif // ifndef BASELIB_HANDLEDSET_H_INCLUDED

