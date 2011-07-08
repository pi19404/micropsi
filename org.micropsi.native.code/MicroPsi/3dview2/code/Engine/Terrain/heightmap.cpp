
#include "Application/stdinc.h"
#include "Engine/Terrain/heightmap.h"

#include <memory.h>
#include "il/il.h"
#include "il/ilu.h"

//---------------------------------------------------------------------------------------------------------------------
CHeightMap::CHeightMap()
{
	m_pcMapData			= 0;
}


//---------------------------------------------------------------------------------------------------------------------
CHeightMap::~CHeightMap()
{
	Clear();
}


//---------------------------------------------------------------------------------------------------------------------
void
CHeightMap::Clear()
{
	if(m_pcMapData != 0)
	{
		delete [] m_pcMapData; 
		m_pcMapData = 0;
	}
}


//---------------------------------------------------------------------------------------------------------------------
/// generate Terrain; Width and Height are in Quads
void
CHeightMap::Generate(int p_iWidth, int p_iHeight)
{
	Clear();

	m_iWidth = p_iWidth;
	m_iHeight = p_iHeight;
	int iNumMapPoints = m_iWidth * m_iHeight;
	m_pcMapData = new unsigned char[iNumMapPoints];
	
	for(int x=0; x<m_iWidth; ++x)
	{
		for(int z=0; z<m_iHeight; ++z)
		{
			m_pcMapData[z*m_iWidth + x] = 1;
		}
	}
}


//---------------------------------------------------------------------------------------------------------------------
/**	
	loads heightmap data from a greyscale bitmap file
	returns true if successful, false otherwise
	existing data is discarded if loading is successful (and only then)

	you can specify the desired iImageHeight and iImageWidth of the iImageHeight map; the image is scaled if necessary
	pass 0/0 as desired iImageHeight and/or iImageWidth to use the size of the bitmap image
*/
bool 
CHeightMap::LoadFromBitmap(const char *p_pcBitmapFilename, int p_iDesiredWidth, int p_iDesiredHeight)
{
    ILuint pixelImage = iluGenImage();
    ilBindImage(pixelImage);

    if (!ilLoadImage((char*) p_pcBitmapFilename))
    {
        DebugPrint("DevIL failed loading image '%s' with '%s'.\n", p_pcBitmapFilename, iluErrorString(ilGetError()));
        iluDeleteImage(pixelImage);
		assert(false);
        return false;
    }

    // make sure image is greyscale
    ILint iformat = ilGetInteger(IL_IMAGE_FORMAT);
    ILint itype = ilGetInteger(IL_IMAGE_TYPE);
//  DebugPrint("Input file format %x, type %x\n", iformat, itype);
    if (iformat != IL_LUMINANCE)
    {
        char *curformat;
        switch(iformat)
        {
        case IL_RGB: curformat = "RGB"; break;
        case IL_RGBA: curformat = "RGBA"; break;
        case IL_BGR: curformat = "BGR"; break;
        case IL_BGRA: curformat = "BGRA"; break;
        case IL_COLOR_INDEX: curformat = "color indexed"; break;
        default: curformat = "unknown"; break;
        }
		DebugPrint("HeightMap: Input file format is %s; needs to be converted to greyscale. Some conversion loss may occur!\n", curformat);
        ilConvertImage(IL_LUMINANCE, itype);
    }

    ILint bit_depth = ilGetInteger(IL_IMAGE_BITS_PER_PIXEL);

    // Color depth must be 8 or 16 bits.
    if (bit_depth != 8 && bit_depth != 16) {
        DebugPrint("input file's bit depth is %d, which is not 8 or 16!\n", bit_depth);
        iluDeleteImage(pixelImage);
        return false;
    }

//    if (bit_depth == 8)
//        m_zscale /= 128.0;

    // make sure image is of size 2^N+1 in both dimensions
    ILuint iImageWidth  = ilGetInteger(IL_IMAGE_WIDTH);
    ILuint iImageHeight = ilGetInteger(IL_IMAGE_HEIGHT);

//    DebugPrint("image iImageWidth = %ld, iImageHeight = %ld, bit depth = %d\n", iImageWidth, iImageHeight, bit_depth);

    ILuint iDesiredWidth = NextPOW2(iImageWidth) + 1;
    ILuint iDesiredHeight = NextPOW2(iImageHeight) + 1;
    if ( (iDesiredWidth != iImageWidth) || (iDesiredHeight != iImageHeight) )
    {
		DebugPrint("Heightmap: Image size is not 2^N+1; needs to be resized. Some conversion loss may occur!\n");
        iluScale(iDesiredWidth, iDesiredHeight, 1);
        iImageWidth = iDesiredWidth;
        iImageHeight = iDesiredHeight;
        DebugPrint("resized height map to %ldx%ld\n", iImageWidth, iImageHeight);
    }


	// everything looks good now, throw away existing map
	Clear();

	m_iWidth  = iImageWidth;
	m_iHeight = iImageHeight;
	int iNumMapPoints = m_iWidth * m_iHeight;
	m_pcMapData = new unsigned char[iNumMapPoints];

    // now dump data into the members
	ilCopyPixels(0, 0, 0, m_iWidth, m_iHeight, 1, IL_LUMINANCE, IL_UNSIGNED_BYTE, m_pcMapData);

    iluDeleteImage(pixelImage);
    return true;
}

//---------------------------------------------------------------------------------------------------------------------
unsigned int 
CHeightMap::NextPOW2(unsigned int p_iValue)
{
	unsigned int iBitpos = 0;
	while ( (1 << iBitpos) + 1u < p_iValue)
	{
		iBitpos++;
		assert(iBitpos < 31);
	}
	return 1<<iBitpos;
}

//---------------------------------------------------------------------------------------------------------------------
void
CHeightMap::SetAllBorderPointsToHeight(unsigned char p_cHeight)
{
	assert(m_pcMapData);
	if(!m_pcMapData)	
	{
		return;
	}

	// unlikely case, but you never know...
	if(m_iHeight < 3  ||  m_iWidth < 3)
	{
		memset(m_pcMapData, p_cHeight, sizeof(char) * m_iHeight * m_iWidth);
		return;
	}

	int iLastRow = (m_iHeight-1) * m_iWidth;
	for(int x=0; x<m_iWidth; ++x)
	{
		m_pcMapData[0		 + x] = p_cHeight;
		m_pcMapData[m_iWidth + x] = p_cHeight;
		m_pcMapData[iLastRow + x] = p_cHeight;
		m_pcMapData[iLastRow - m_iWidth + x] = p_cHeight;
	}

	for(int z=0; z<m_iHeight*m_iWidth; z+=m_iWidth)
	{
		m_pcMapData[z]				 = p_cHeight;
		m_pcMapData[z+1]			 = p_cHeight;
		m_pcMapData[z + m_iWidth -1] = p_cHeight;
		m_pcMapData[z + m_iWidth -2] = p_cHeight;
	}
}

//---------------------------------------------------------------------------------------------------------------------
void
CHeightMap::WrapBordersAround()
{
	assert(m_pcMapData);
	if(!m_pcMapData)	
	{
		return;
	}

	int iLastRow = (m_iHeight-1) * m_iWidth;
	for(int x=0; x<m_iWidth; ++x)
	{
		m_pcMapData[x] = m_pcMapData[iLastRow + x];
	}

	for(int z=0; z<m_iHeight*m_iWidth; z+=m_iWidth)
	{
		m_pcMapData[z] = m_pcMapData[z + m_iWidth -1];
	}
}
//---------------------------------------------------------------------------------------------------------------------
