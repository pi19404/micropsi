//----------------------------------------------------------------------------
// skybox-shader
//----------------------------------------------------------------------------

// Matrices

texture2D	c_tDiffuseMap;

float4x3    c_mWorld			: WORLD;
float4x4    c_mWorldViewProj	: WORLDVIEWPROJECTION;

//----------------------------------------------------------------------------
struct VertPT
{
    float4  vPos             : POSITION;
    float2  vTex0            : TEXCOORD0;
};
//----------------------------------------------------------------------------
VertPT 
VShade(VertPT i)
{
    VertPT   o;
     
	o.vPos = mul(float4(i.vPos.xyz, 1.0f), c_mWorldViewProj);
    o.vTex0  = i.vTex0.xy;

    return o;
}
//----------------------------------------------------------------------------
VertexShader vsShader = compile vs_1_1 VShade();
//----------------------------------------------------------------------------
// Techniques specs follow
//----------------------------------------------------------------------------
technique _staticmesh
{
    pass p0
    {
        VertexShader = (vsShader);
        PixelShader  = 0;

		Texture[0] = (c_tDiffuseMap);

		AlphaTestEnable = false;
		ZEnable = false;
		ZWriteEnable = false;

		CullMode = CCW;

        ColorOp[0] = SelectArg1;
        ColorArg1[0] = Texture;
        ColorArg2[0] = Diffuse;

        ColorOp[1] = Disable;
        

        AlphaOp[0] = SelectArg1;
        AlphaArg1[0] = Texture;

        AlphaOp[1] = Disable;


		AddressU[0] = Clamp;
		AddressV[0] = Clamp;

		AlphaBlendEnable = false;
    }
}

