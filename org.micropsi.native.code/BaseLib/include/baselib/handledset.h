/*
	Ein Handled Set ist eine Menge von Objekten, ähnlich einem Array. Die einzelnen Elemente des Sets werden 
	aber nicht über ihren Index, sondern über Handles angesprochen. Beim erzeugen eines neuen Elements im Set 
	erhält man ein Handle. Wird das Element aus dem Set entfernt, wird das Handle ungültig. Wir später mit 
	diesem ungültigen Handle auf das Set zugegriffen, wird das Handle als ungültig erkannt. 
	Die Elemente des Sets haben keine verläßliche Reihenfolge, lassen sich aber iterieren. 

	Die Handles werden nicht 0; 0 kann daher immer als ungültiges Handle verwendet werden.
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


	/// Iterationsschritt; liefert true, wenn der Out-Parameter das nächste Element enthält; false bei Ende der Iteration
	bool			Iterate(unsigned long& p_iIterator, _ElementType& po_rxElement) const;

	/// Iterationsschritt; liefert true, wenn der Out-Parameter das nächste Element enthält; false bei Ende der Iteration
	bool			Iterate(unsigned long& p_iIterator, _ElementType*& po_rpxElement) const;

	/// erzeugt einen neuen Eintrag, liefert Handle auf (noch leeren) Eintrag
	unsigned long	PushEntry();


	/// erzeugt neues Handle und weist diesem das übergebene Element zu
	unsigned long	PushEntry(const _ElementType& p_rxElement);


	/// löscht einen Eintrag, das Handle wird dabei ungültig; liefert true bei Erfolg, false bei ungültigem Handle
	bool			DeleteEntry(unsigned long p_uiHandle);


	/// löscht das gesamte Set (dadurch wird die Zahl Slots nicht kleiner, da die Generation-Info erhalten bleibt!)
	void			Clear();


	/// liefert true, wenn dieses Handle gültig ist
	bool			IsValid(unsigned long p_uiHandle) const;


	/// liefet ein Element zurück; vorher sollte geprüft werden, ob Element Handle gültig ist!
	_ElementType&	Element(unsigned long p_uiHandle) const;

	/// liefet ein Element (als Pointer) zurück; liefert 0, wenn Handle ungültig war
	_ElementType*	ElementPtr(unsigned long p_uiHandle) const;

	/// weist einem Element einen Wert zu; liefet false, wenn Handle ungültig war
	bool			SetElement(unsigned long p_uiHandle, const _ElementType& p_krxElement) const;

	/// liefert die Größe des Sets (= Anzahl Elemente)
	unsigned int	Size() const;

	/// liefert ein ungültiges Handle zurück
	static unsigned long InvalidHandle();

private:

    CreateElementFunction     m_fpCreateElement;    ///< Funktion zum Anlegen von Elementen
    DestroyElementFunction    m_fpDestroyElement;   ///< Funktion zum Löschen von Elementen

    static _ElementType* __cdecl StdCreateElement();                ///< Default-Create-Funktion
    static void __cdecl StdDestroyElement(_ElementType* pElement);  ///< Default-Lösch-Funktion


	class CElement
	{
	public:
		CElement()			{ m_pxElementPtr = 0;	m_uiGeneration = 1; }

		_ElementType*		m_pxElementPtr;			///< Zeiger auf eigentliches Element; ist 0, wenn der Slot leer ist
		unsigned short		m_uiGeneration;			///< Generationszähler
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


    int             GetFreeSlotIdx();                           ///< liefert einen freien slot zurück


	CDynArray<CElement, uiGrowthExp, true>	m_axElements;		///< Array mit Elementen
	CDynArray<int, uiGrowthExp, false>		m_aiEmptySlots;		///< Array mit Indizes von leeren (=wiederverwendbaren) Slots
};


#include "baselib/handledset.inl"

#endif // ifndef BASELIB_HANDLEDSET_H_INCLUDED

