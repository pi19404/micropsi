/*******************************************************************************
 MoviePlayer.h - Spielt beliebige Filmdateien ab, insofern auf dem 
 Rechner der passende Codec installiert ist.
 Die steuernde Applikation sollte periodisch IsPlaying() aufrufen, um zu über-
 prüfen, ob der Film mittlerweile beendet ist.
*******************************************************************************/
#pragma once

#ifndef MOVIEPLAYER_H_INCLUDED
#define MOVIEPLAYER_H_INCLUDED

#include "baselib/comobjectptr.h"

struct IGraphBuilder;
typedef struct HWND__* HWND;

class CMoviePlayer
{
public:
	CMoviePlayer();
	~CMoviePlayer();

	bool PlayFile(HWND hndWindow, const char* pcFilename);
	bool IsPlaying(); 
	void Stop();

private:
	CComObjectPtr<IGraphBuilder>    m_spxGraph;
	bool			                m_bPlaying;
};

#endif // MOVIEPLAYER_H_INCLUDED