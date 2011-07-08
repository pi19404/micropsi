#ifndef MOUSECURSOR_H_INCLUDED
#define MOUSECURSOR_H_INCLUDED

namespace UILib
{

class CMouseCursor
{
public:

	/// Cursortypen, v�llig analog zu den Windows-Cursortypen
	enum CursorType
	{
		CT_None,				///< kein Cursor; f�r Fenster bedeutet das: benutze den Cursor deines Elternfensters
		CT_Arrow,				///< Standardcursor: Pfeil
		CT_SizeNS,				///< Doppelpfeil nach Norden und S�den
		CT_SizeWE,				///< Doppelpfeil nach Osten und Westen
		CT_SizeNWSE,			///< Doppelpfeil nach Nordwesten und S�dosten
		CT_SizeNESW,			///< Doppelpfeil nach Nordosten und S�dwesten
		CT_Wait,				///< Warte-Cursor (meist Sanduhr)
		CT_Cross,				///< Fadenkreuz
		CT_No,					///< Durchgeschrichener Cursor
		CT_IBeam,				///< I-F�rmiger Cursor f�r Texteingabe
		CT_AppStarting,			///< Applikation startet gerade (Pfeil + Sanduhr)
		CT_Hand,				///< Hand
		CT_Help,				///< Pfeil und Fragezeichen
		CT_UpArrow,				///< Pfeil nach oben
		CT_InvalidCursor = -1	///< ung�ltiger Cursor
	};


	static const char*	ms_pcCurrentCursor;				///< aktueller Cursor als String

	/// setzt den angegebenen Cursor
	static void SetCursor(CursorType p_eType);

	/// setzt den Standard-Cursor (Pfeil)
	static void SetStandardCursor();
};

} // namespace UILib

#endif // ifndef MOUSECURSOR_H_INCLUDED

