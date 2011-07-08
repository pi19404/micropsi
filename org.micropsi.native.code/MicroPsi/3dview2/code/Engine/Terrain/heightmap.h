#pragma once
#ifndef HEIGHTMAP_H_INCLUDED
#define HEIGHTMAP_H_INCLUDED

class CHeightMap
{
public: 

	CHeightMap();
	~CHeightMap();

	void			Clear();
	int				GetHeight(int p_iX, int p_iZ) const;	

	void			Generate(int p_iWidth, int p_iHeight);
	bool			LoadFromBitmap(const char *p_pcBitmapFilename, int p_iDesiredWidth = 0, int p_iDesiredHeight = 0);

	void			SetAllBorderPointsToHeight(unsigned char p_cHeight);
	void			WrapBordersAround();


	int				GetHeight() const;
	int				GetWidth() const;

private:

	static unsigned int NextPOW2(unsigned int p_iValue);

	unsigned char*		m_pcMapData;			///< Height Map
	int					m_iWidth;				///< width of map (in values)
	int					m_iHeight;				///< height of map (in values)

};

#include "heightmap.inl"

#endif // ifndef HEIGHTMAP_H_INCLUDED

