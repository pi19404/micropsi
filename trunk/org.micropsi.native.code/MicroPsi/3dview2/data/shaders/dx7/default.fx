//-----------------------------------------------------------------------------------------------------------------------------------------
// normaler diffuselight-shader
//-----------------------------------------------------------------------------------------------------------------------------------------
#include "_skinning.fx"
#include "_lighting.fx"

int					c_iNumBones = 0;

// Matrizen
float4x3			c_mWorld											: WORLD;				///< für statische Meshes
float4x3			c_amWorldMatrixArray[SKINNING_MATRIX_PALETTE_SIZE]	: WORLDMATRIXARRAY;		///< für geskinnte Meshes

float4x4			c_mWorldViewProj									: WORLDVIEWPROJECTION;
float4x4			c_mViewProj											: VIEWPROJECTION;

// Lichtparameter
float4				c_vLightDir = {1.0f, 0.0f, 0.0f, 1.0f};		///< Lichtrichtung
float4				c_vLightDiffuse = {1, 1, 1, 1};				///< Farbe des Lichts
shared float		c_fLightFactor = 1;							///< Helligkeitswert (abhängig von Bodenhelligkeit)

// Texturen
texture2D			c_tDiffuseMap;

/// sonstiges
shared bool			c_bZWriteZTest = true;

//-----------------------------------------------------------------------------------------------------------------------------------------
sampler2D c_sDiffuseMapSampler =
sampler_state
{
	Texture = <c_tDiffuseMap>;
	AddressU = Wrap;
	AddressV = Wrap;
};

//-----------------------------------------------------------------------------------------------------------------------------------------
struct VS_INPUT_STATICMESH
{
    float3  vPos             : POSITION;
    float3  vNormal          : NORMAL;
    float2  vTex0            : TEXCOORD0;
};

struct VS_INPUT_SKINNEDMESH
{
    float3  vPos             : POSITION;
    float3  vNormal          : NORMAL;
    float2  vTex0            : TEXCOORD0;
    float4  vBlendWeights    : BLENDWEIGHT;
    float4  vBlendIndices    : BLENDINDICES;
};

struct VS_OUTPUT
{
    float4  vPos     : POSITION;
    float2  vTex0    : TEXCOORD0;
    float4  vDiffuse : COLOR0;
};


//-----------------------------------------------------------------------------------------------------------------------------------------
// VertexShader für geskinnte Meshes
VS_OUTPUT 
VShade_SkinnedMesh(VS_INPUT_SKINNEDMESH i, uniform int iNumBones)
{
	VS_OUTPUT o;

	CalcSkinning(i.vPos, i.vNormal, i.vBlendWeights, i.vBlendIndices, c_iNumBones, c_amWorldMatrixArray);
    
    // Transformation der Position auf den Bildschirm
    o.vPos = mul(float4(i.vPos.xyz, 1.0f), c_mViewProj);
    
	// Texturkoordinaten durchschleifen    
    o.vTex0 = i.vTex0.xy;

	// DiffuseColor setzen
	o.vDiffuse = c_vLightDiffuse * CalcBrightness(normalize(i.vNormal), c_vLightDir.xyz);

    return o;
};
//-----------------------------------------------------------------------------------------------------------------------------------------
VS_OUTPUT 
VShade_StaticMesh(VS_INPUT_STATICMESH i)
{
    VS_OUTPUT   o;
     
    // Transformation der Position auf den Bildschirm
    o.vPos  = mul(float4(i.vPos, 1.0f), c_mWorldViewProj);

	// Texturkoordinaten durchschleifen    
    o.vTex0 = i.vTex0.xy;

	// DiffuseColor und TexCoords im Outputvektor setzen
    float3 vWorldNormal = mul(i.vNormal, c_mWorld);
	o.vDiffuse = c_vLightDiffuse * CalcBrightness(normalize(vWorldNormal), c_vLightDir.xyz);

    return o;
};
//-----------------------------------------------------------------------------------------------------------------------------------------

VertexShader vsSkinnedMesh[4] = 
{ 
	compile vs_1_1 VShade_SkinnedMesh(1),
    compile vs_1_1 VShade_SkinnedMesh(2),
    compile vs_1_1 VShade_SkinnedMesh(3),
    compile vs_1_1 VShade_SkinnedMesh(4)
};

VertexShader vsStaticMesh = 
	compile vs_1_1 VShade_StaticMesh();
	
//-----------------------------------------------------------------------------------------------------------------------------------------
technique _staticmesh
{
    pass p0
    {
        VertexShader = <vsStaticMesh>;
        PixelShader  = 0;

 		AlphaTestEnable 	= true;
 		AlphaRef 			= 130;

		AlphaBlendEnable	= true;
		SrcBlend			= SRCALPHA;
		DestBlend			= INVSRCALPHA;
		
        ZEnable = <c_bZWriteZTest>;
		ZWriteEnable = <c_bZWriteZTest>;
        
		Sampler[0] = <c_sDiffuseMapSampler>;

		cullmode = cw;

		// color pipe
        ColorOp[0] 	 = Modulate;
        ColorArg1[0] = Texture;
        ColorArg2[0] = Diffuse;

        ColorOp[1] = Disable;
        ColorOp[2] = Disable;
        
        
        // alpha pipe
        AlphaOp[0] = SelectArg1;
        AlphaArg1[0] = Texture;

        AlphaOp[1] = Disable;
        AlphaOp[2] = Disable;
    }
}

technique _skinnedmesh
{
    pass p0
    {
	    VertexShader = <vsSkinnedMesh[c_iNumBones - 1]>;
        PixelShader  = 0;

 		AlphaTestEnable 	= true;
 		AlphaRef 			= 130;

		AlphaBlendEnable	= true;
		SrcBlend			= SRCALPHA;
		DestBlend			= INVSRCALPHA;

        ZEnable = <c_bZWriteZTest>;
		ZWriteEnable = <c_bZWriteZTest>;

		cullmode = cw;
        
		Sampler[0] = <c_sDiffuseMapSampler>;

		// color pipe
        ColorOp[0] = Modulate;
        ColorArg1[0] = Texture;
        ColorArg2[0] = Diffuse;

        ColorOp[1] = Disable;
        ColorOp[2] = Disable;
        
        
        // alpha pipe
        AlphaOp[0] = SelectArg1;
        AlphaArg1[0] = Texture;

        AlphaOp[1] = Disable;
        AlphaOp[2] = Disable;
    }
}

//-----------------------------------------------------------------------------------------------------------------------------------------
	