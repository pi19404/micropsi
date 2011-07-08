
#include "Application/stdinc.h"
#include "Engine/Terrain/materialmap.h"

#include <memory.h>
#include "il/il.h"
#include "il/ilu.h"

//---------------------------------------------------------------------------------------------------------------------
CMaterialMap::CMaterialMap()
{
	m_pcMaterialData	= 0;
}


//---------------------------------------------------------------------------------------------------------------------
CMaterialMap::~CMaterialMap()
{
	Clear();
}

//---------------------------------------------------------------------------------------------------------------------
void
CMaterialMap::Clear()
{
	if(m_pcMaterialData != 0)
	{
		delete [] m_pcMaterialData; 
		m_pcMaterialData = 0;
	}
}

//---------------------------------------------------------------------------------------------------------------------
/**
	loads material map data from a greyscale bitmap file
	returns true if successful, false otherwise
	existing data is discarded if loading is successful (and only then)

	you can specify the desired iImageHeight and iImageWidth of the material map; the image is scaled if necessary
	pass 0/0 as desired iImageHeight and/or iImageWidth to use the size of the bitmap image
*/
bool		
CMaterialMap::LoadFromBitmap(const char *p_pcBitmapFilename, int p_iDesiredWidth, int p_iDesiredHeight)
{
    ILuint pixelImage = iluGenImage();
    ilBindImage(pixelImage);

    if (!ilLoadImage((char*) p_pcBitmapFilename))
    {
        DebugPrint("DevIL failed loading image '%s' with '%s'.\n", p_pcBitmapFilename, iluErrorString(ilGetError()));
        iluDeleteImage(pixelImage);
        return false;
    }

    // make sure image is greyscale
    ILint iformat = ilGetInteger(IL_IMAGE_FORMAT);
    ILint itype = ilGetInteger(IL_IMAGE_TYPE);
//	DebugPrint("Input file format %x, type %x\n", iformat, itype);

	assert(iformat == IL_COLOR_INDEX);	// Material Map is not an Color-Indexed Image!

    // make sure image is of size 2^N+1 in both dimensions
    ILuint iImageWidth  = ilGetInteger(IL_IMAGE_WIDTH);
    ILuint iImageHeight = ilGetInteger(IL_IMAGE_HEIGHT);

//    DebugPrint("image iImageWidth = %ld, iImageHeight = %ld\n", iImageWidth, iImageHeight);

	ILuint iDesiredWidth	= (p_iDesiredWidth > 0)  ? p_iDesiredWidth  : iImageWidth;
	ILuint iDesiredHeight	= (p_iDesiredHeight > 0) ? p_iDesiredHeight : iImageHeight;

    if ( (iDesiredWidth != iImageWidth) || (iDesiredHeight != iImageHeight) )
    {
        DebugPrint("material image sizes are not same size as terrain, resizing.  Some conversion loss may occur!\n");
        iluScale(iDesiredWidth, iDesiredHeight, 1);
        iImageWidth = iDesiredWidth;
        iImageHeight = iDesiredHeight;
        DebugPrint("resized material map to %ldx%ld\n", iImageWidth, iImageHeight);
    }

	// everything looks good now, throw away existing map
	Clear();

	m_iWidth = iImageWidth;
	m_iHeight = iImageHeight;

	int iNumMapTiles = iImageWidth * iImageHeight;
	m_pcMaterialData = new unsigned char[iNumMapTiles];

	ilCopyPixels(0, 0, 0, m_iWidth, m_iHeight, 1, IL_COLOR_INDEX, IL_UNSIGNED_BYTE, m_pcMaterialData);

    iluDeleteImage(pixelImage);
	return true;
}

//---------------------------------------------------------------------------------------------------------------------
