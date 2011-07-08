#pragma once
#ifndef TERRAINMATERIAL_H_INCLUDED
#define TERRAINMATERIAL_H_INCLUDED

#include "e42/core/TextureFactory.h"

#include <string>

class CTerrainMaterial
{
public:

	CTerrainMaterial();
	~CTerrainMaterial();

	void	Clear();
	bool	Load(CEngineController* p_pxEngineController, const std::string& p_srTextureName, int p_iLowResWidth);

	bool					m_bIsValid;				///< true, if this material has been defined properly
	TTextureHandle			m_hTexture;				///< texture for this material
	unsigned int*			m_piLowResImage;		///< very low resolution image of this texture
	std::string				m_sTextureName;			///< original texture name
};

#endif // ifndef TERRAINMATERIAL_H_INCLUDED

