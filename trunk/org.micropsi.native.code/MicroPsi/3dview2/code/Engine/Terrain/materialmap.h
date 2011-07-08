#pragma once
#ifndef MATERIALMAP_H_INCLUDED
#define MATERIALMAP_H_INCLUDED

class CMaterialMap
{
public: 

	CMaterialMap();
	~CMaterialMap();

	void			Clear();
	bool			LoadFromBitmap(const char *p_pcBitmapFilename, int p_iDesiredWidth = 0, int p_iDesiredHeight = 0);

	int				GetMaterial(int p_iX, int p_iZ) const;
	int				IsMaterialOrNeighborIsMaterial(int p_iX, int p_iZ, int p_iMaterial) const;

	int				GetHeight() const;
	int				GetWidth() const;

private:

	unsigned char*		m_pcMaterialData;		///< Material Map
	int					m_iWidth;				///< width of map (in values)
	int					m_iHeight;				///< height of map (in values)
};

#include "materialmap.inl"

#endif // ifndef MATERIALMAP_H_INCLUDED

