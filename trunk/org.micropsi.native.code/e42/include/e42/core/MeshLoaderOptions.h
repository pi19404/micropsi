#pragma once

#ifndef MESHLOADEROPTIONS_H_INCLUDED
#define MESHLOADEROPTIONS_H_INCLUDED

#include <string>
#include "baselib/geometry/CVector.h"

class CMeshLoaderOptions
{
public:
	CMeshLoaderOptions();

	std::string	m_sShaderPath;
	std::string	m_sTexturePath;

	CVec3		m_vStaticShadowVolumeExtrusion;
	bool		m_bCreateEdgeQuads;						///< für dynamische StencilVolumeErzeugung
	float		m_fShadowVolumeShrink;					///< Faktor um den das Schattenvolumen verkleinert wird, damit StencilShadowArtefakte vermieden werden

	float		m_fLevelOfDetailFactor;					///< Prozentwert, der Angibt wieviele Polygone des Originalmeshes behalten werden sollen

	bool		m_bGenerateNormals;						///< Normalenvektoren berechnen (falls nicht in der Datei drin)
	bool		m_bGenerateTangents;					///< Tangentenvektoren für UVSet 0 berechnen (falls nicht in der Datei drin)
	bool		m_bGenerateBinormals;					///< Binormalenvektoren für UVSet 0 berechnen (falls nicht in der Datei drin)

	bool		m_bGenerateVertexElementsByShaderInput;	///< sucht im Shader nach einer Deklaration für die benötigten Vertexkomponenten

	bool		m_bRemoveEmptyFrames;					///< löscht alle Frames raus, die keine Children und keine Meshcontainer haben 
														///< (!!! Vorsicht bei Animationen und Markern !!!)

    bool		m_bSkipNormals;							///< entfernt die Normalenvektoren aus den VertexStreams
	bool		m_bLoadResources;						///< Laden von Texturen und Shadern
	bool		m_bOptimizeMesh;						///< optimiert das Mesh nach dem Laden (VertexCacheOptimierung)

	DWORD		m_dwMeshOptions;						///< Kombination von D3DXMESH-Flags

	std::string	GetIDString();
};

#endif MESHLOADEROPTIONS_H_INCLUDED