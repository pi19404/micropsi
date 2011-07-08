
#pragma once
#ifndef MICROPSIUTILS_H_INCLUDED
#define MICROPSIUTILS_H_INCLUDED

#include "baselib/geometry/CVector.h"

namespace Utils
{

CVec3 PsiPos2Engine(const CVec3& p_xrVector);
CVec3 EnginePos2Psi(const CVec3& p_xrVector);
float PsiAngle2Engine(float p_fAngle);
float EngineAngle2Psi(float p_fAngle);

}

#include "micropsiutils.inl"

#endif // MICROPSIUTILS_H_INCLUDED
