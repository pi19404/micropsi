#include "Application/stdinc.h"
#include "Engine/Terrain/terrainmaterial.h"

#include "baselib/filelocator.h"

#include "e42/core/EffectShader.h"
#include "baselib/geometry/Plane.h"
#include "e42/E42Application.h"


#include "il/il.h"
#include "il/ilu.h"

#include "Utilities/systemutils.h"

using std::string;


//---------------------------------------------------------------------------------------------------------------------
CTerrainMaterial::CTerrainMaterial()
{
	m_bIsValid = false;
	m_piLowResImage = 0;
}

//---------------------------------------------------------------------------------------------------------------------
CTerrainMaterial::~CTerrainMaterial()
{
	Clear();
}

//---------------------------------------------------------------------------------------------------------------------
void
CTerrainMaterial::Clear()
{
	m_bIsValid = false;
	if(m_piLowResImage)
	{
		delete [] m_piLowResImage;
	}
	if(m_hTexture)
	{
		m_hTexture.Release();
	}
}

//---------------------------------------------------------------------------------------------------------------------
/**
	associates a texture with this material
	this function creates the texture and a low res version of the texture which is used by the chunks to create 
	their base textures
*/
bool		
CTerrainMaterial::Load(CEngineController* p_pxEngineController, const string& p_srTextureName, int p_iLowResWidth)
{
	Clear();

	m_sTextureName = p_srTextureName;
	m_hTexture = p_pxEngineController->GetTextureFactory()->CreateTextureFromFile(string("texture>") + p_srTextureName);

	// create 4x4 pixel image from texture

	ILuint pixelImage = iluGenImage();
    ilBindImage(pixelImage);

	if (!ilLoadImage((char*) p_pxEngineController->GetFileLocator()->GetPath("texture>" + p_srTextureName).c_str()))
    {
		iluDeleteImage(pixelImage);
		assert(false);
        return false;
    }

    ILint iformat = ilGetInteger(IL_IMAGE_FORMAT);
    ILint itype = ilGetInteger(IL_IMAGE_TYPE);
    ILint bit_depth = ilGetInteger(IL_IMAGE_BITS_PER_PIXEL);

	if(iformat != IL_BGRA  || bit_depth != 32)
	{
		ilConvertImage(IL_BGRA, itype);
	}
    iformat = ilGetInteger(IL_IMAGE_FORMAT);
    bit_depth = ilGetInteger(IL_IMAGE_BITS_PER_PIXEL);
	assert(bit_depth == 32);

    ILuint iImageWidth  = ilGetInteger(IL_IMAGE_WIDTH);
    ILuint iImageHeight = ilGetInteger(IL_IMAGE_HEIGHT);

//    DebugPrint("image iImageWidth = %ld, iImageHeight = %ld, bit depth = %d\n", iImageWidth, iImageHeight, bit_depth);

	ILuint iDesiredWidth  = p_iLowResWidth;
    ILuint iDesiredHeight = p_iLowResWidth;

    if ( (iDesiredWidth != iImageWidth) || (iDesiredHeight != iImageHeight) )
    {
		iluImageParameter(ILU_FILTER, ILU_SCALE_MITCHELL);
        iluScale(iDesiredWidth, iDesiredHeight, 1);
		
		iImageWidth  = ilGetInteger(IL_IMAGE_WIDTH);
		iImageHeight = ilGetInteger(IL_IMAGE_HEIGHT);
		assert(iImageWidth  == iDesiredWidth);
		assert(iImageHeight == iDesiredHeight);
//        DebugPrint("resized image iImageWidth = %ld, iImageHeight = %ld\n", iImageWidth, iImageHeight);
    }
	int iSize  = iImageWidth * iImageHeight;
	m_piLowResImage = new unsigned int[iSize];

    unsigned int* piData = (unsigned int*) ilGetData();
	for(unsigned int x=0; x<iImageWidth; ++x)
	{
		for(unsigned int y=0; y<iImageHeight; ++y)
		{
			m_piLowResImage[y*iImageWidth + x] = piData [y * iImageWidth + x];
		}
	}

	// for debugging purposes: write image to disc ;)
//	ilSaveImage((char*) p_pxEngineController->GetFileLocator()->GetPath("texture>" + p_srTextureName + ".small.tga").c_str());

	iluDeleteImage(pixelImage);
	m_bIsValid = true;

	return true;
}
//---------------------------------------------------------------------------------------------------------------------
