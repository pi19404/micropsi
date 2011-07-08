//----------------------------------------------------------------------------
// Partikel-Shader
//----------------------------------------------------------------------------

float4x3    c_mWorldView		: WORLDVIEW;
float4x4    c_mProjection		: PROJECTION;

texture2D	c_tDiffuseMap;

//----------------------------------------------------------------------------
struct VS_INPUT
{
    float3  vPos            : POSITION;
    float4	vDiffuse		: COLOR;
    float2  vTex0          	: TEXCOORD0;
    float   fSize			: BLENDWEIGHT0;
    float   fRotation		: BLENDWEIGHT1;
};

struct VS_OUTPUT
{
    float4  vPos     : POSITION;
    float4  vDiffuse : COLOR;
    float2  vTex0    : TEXCOORD0;
};
//----------------------------------------------------------------------------
float3 
CalcDir(float fAngle)
{
	return float3(sin(fAngle), cos(fAngle), 0);
}
//----------------------------------------------------------------------------
VS_OUTPUT 
VShade(VS_INPUT i)
{
    VS_OUTPUT   o;
    
    float3 vCornerDir = CalcDir(i.fRotation);

	float3 v = mul(float4(i.vPos, 1), c_mWorldView);
	o.vPos = mul(float4(v + i.fSize * vCornerDir, 1), c_mProjection);
	o.vTex0 = i.vTex0;
	o.vDiffuse = i.vDiffuse;

    return o;
}
//----------------------------------------------------------------------------
VertexShader vsShader = compile vs_1_1 VShade();

//----------------------------------------------------------------------------
// Techniques specs follow
//----------------------------------------------------------------------------
technique standard
{
	pass p0
    {
        VertexShader = (vsShader);
        PixelShader  = 0;

		ZEnable = true;
		ZWriteEnable = false;

		Texture[0] = (c_tDiffuseMap);

        ColorOp[0] = SelectArg1;
        ColorArg1[0] = Texture;
        ColorArg2[0] = Diffuse;

        ColorOp[1] = Disable;
        

        AlphaOp[0] = Modulate;
        AlphaArg1[0] = Texture;
        AlphaArg2[0] = Diffuse;

        AlphaOp[1] = Disable;

		AddressU[0] = Wrap;
		AddressV[0] = Wrap;
		
		AlphaTestEnable = true;
		AlphaRef = 0;
		AlphaFunc = Greater;

		AlphaBlendEnable = true;
		SrcBlend = srcalpha;
		DestBlend = invsrcalpha;
    }
    
    pass undo
    {
		ZWriteEnable = true;
    }
}
//----------------------------------------------------------------------------

