#include "stdafx.h"

#include "e42/core/D3DXMacro.h"

#include "baselib/utils.h"

//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXMacro::CD3DXMacro()
{
	this->Name = NULL;
	this->Definition = NULL;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXMacro::CD3DXMacro(const D3DXMACRO& rxD3DMacro)
{
	this->Name = NULL;
	this->Definition = NULL;

	SetName(rxD3DMacro.Name);
	SetDefinition(rxD3DMacro.Definition);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXMacro::CD3DXMacro(const CD3DXMacro& rxD3DMacro)
{
	this->Name = NULL;
	this->Definition = NULL;

	SetName(rxD3DMacro.Name);
	SetDefinition(rxD3DMacro.Definition);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXMacro::~CD3DXMacro()
{
	Clear();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXMacro& 
CD3DXMacro::operator=(const D3DXMACRO& rxD3DMacro)
{
	SetName(rxD3DMacro.Name);
	SetDefinition(rxD3DMacro.Definition);

	return *this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXMacro& 
CD3DXMacro::operator=(const CD3DXMacro& rxD3DMacro)
{
	SetName(rxD3DMacro.Name);
	SetDefinition(rxD3DMacro.Definition);

	return *this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CD3DXMacro::Clear()
{
	SetName(NULL);
	SetDefinition(NULL);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CD3DXMacro::SetName(const char* pcName)
{
    Utils::DeletePCharString(__super::Name);
    __super::Name = 
        Utils::ClonePCharString(pcName);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
const char* 
CD3DXMacro::GetName() const
{
	return this->Name;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CD3DXMacro::SetDefinition(const char* pcDefinition)
{
    Utils::DeletePCharString(__super::Definition);
    __super::Definition = 
        Utils::ClonePCharString(pcDefinition);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
const char* 
CD3DXMacro::GetDefinition() const
{
	return this->Definition;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
D3DXMACRO& 
CD3DXMacro::D3DXMacro()
{
	return *(D3DXMACRO*)this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
const D3DXMACRO& 
CD3DXMacro::D3DXMacro() const
{
	return *(const D3DXMACRO*)this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
