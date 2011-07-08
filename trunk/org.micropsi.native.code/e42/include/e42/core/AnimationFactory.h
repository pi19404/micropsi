#pragma once

#ifndef E42_ANIMATIONFACTORY_H_INCLUDED
#define E42_ANIMATIONFACTORY_H_INCLUDED

#include "e42/stdinc.h"

#include <d3dx9mesh.h>

#include "e42/core/ResourceHandles.h"
#include "e42/core/ResourceFactory.h"

#include "baselib/comobjectptr.h"

class CEngineController;

class CAnimationFactory : public CResourceFactory
{
private:
	CEngineController*	m_pxEngineController;

	void				DestroyResource(void* pxResource);

public:
	CAnimationFactory(CEngineController* pxEngineController);
	~CAnimationFactory();

	/// lädt eine Animation aus einem X-File (Mesh wird ignoriert)
	TAnimationHandle CreateAnimationFromFile(const std::string& sFilename);

	/// stellt eine existierende Animation unter die Kontrolle der Factory
	TAnimationHandle RegisterAnimation(CComObjectPtr<ID3DXAnimationSet> spxAnimationSet, const std::string& sResourceID);
};

#endif // E42_ANIMATIONFACTORY_H_INCLUDED