#pragma once

#ifndef E42_VERTEXDECLARATIONFACTORY_H_INCLUDED
#define E42_VERTEXDECLARATIONFACTORY_H_INCLUDED

#include "e42/stdinc.h"

#include "e42/core/ResourceHandles.h"
#include "e42/core/ResourceFactory.h"

class CEngineController;
class CD3DVertexDeclaration;

class CVertexDeclarationFactory : public CResourceFactory
{
private:
    CEngineController*  m_pxEngineController;

    void		DestroyResource(void* pxResource);

	std::string	GetResourceID(DWORD dwFVF) const;


public:

    CVertexDeclarationFactory(CEngineController* pxEngineController);
    ~CVertexDeclarationFactory();

    TVertexDeclarationHandle CreateVertexDeclaration(DWORD dwFVF);
    TVertexDeclarationHandle CreateVertexDeclaration(const CD3DVertexDeclaration& rxVertexDeclaration, const std::string& sName = std::string());
};


#endif // E42_VERTEXDECLARATIONFACTORY_H_INCLUDED