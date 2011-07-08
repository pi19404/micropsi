#include "stdafx.h"
#include "GameLib/Physics/DynamicsSimulation.h"
#include "GameLib/Physics/DynamicsObject.h"

#include "GameLib/Physics/DynamicsWorld.h"
#include "GameLib/Physics/DynamicsObject.h"

//---------------------------------------------------------------------------------------------------------------------
CDynamicsSimulation::CDynamicsSimulation()
{
	m_pWorld = new CDynamicsWorld();
	m_pSpace = new dHashSpace(0);
	m_pContactJointGroup = new dJointGroup();
}
//---------------------------------------------------------------------------------------------------------------------
CDynamicsSimulation::~CDynamicsSimulation()
{
	delete m_pContactJointGroup;
	delete m_pSpace;
	delete m_pWorld;
}
//---------------------------------------------------------------------------------------------------------------------
void	
CDynamicsSimulation::Update(double dDeltaTime)
{
	if(dDeltaTime > 0.0)
	{
		m_pSpace->collide(this, CDynamicsSimulation::PotentialCollisionCallback);
		m_pWorld->Update(dDeltaTime);
		m_pContactJointGroup->empty();
	}
}
//---------------------------------------------------------------------------------------------------------------------
/**
	wird gerufen wenn zwei Objekt potientiell kollidieren
	überprüft, ob sie wirklich kollidieren und generiert Contactjoints aus den Berührungspunkten 
*/
void 
CDynamicsSimulation::PotentialCollisionCallback (void *data, dGeomID o1, dGeomID o2)
{
	CDynamicsSimulation* pxThis = (CDynamicsSimulation*) data;

	if(dGeomIsSpace(o1) || dGeomIsSpace(o2))
	{
		// mindestens ein Partner ist ein Space --> überprüfen, ob Objekte aus einem Space mit Objekten aus dem anderen Space bzw. dem zweiten Objekt
		// potientiell kollidieren; ruft rekursiv wieder diese Funktion
		dSpaceCollide2(o1, o2, data, &PotentialCollisionCallback);

		// jetzt die Objekte innerhalb der Spaces kollidieren
		if(dGeomIsSpace(o1))
		{
			dSpaceCollide((dSpaceID) o1, data, &PotentialCollisionCallback);
		}
		if(dGeomIsSpace(o2))
		{
			dSpaceCollide((dSpaceID) o2, data, &PotentialCollisionCallback);
		}
	}
	else
	{
		// zwei "echte" Körper (keine Spaces) kollidieren 

		dBodyID b1 = dGeomGetBody(o1);
		dBodyID b2 = dGeomGetBody(o2);

		// wenn beide durch einen Joint (ausser Contactjoints) verbunden sind generieren wir natürlich keine Kontakte
		if(b1 && b2 && dAreConnectedExcluding(b1, b2, dJointTypeContact)) 
			return;
		
		//
		dContact contact[MAXCONTACTS];
		int iNumContacts = dCollide(o1, o2, MAXCONTACTS, &contact[0].geom, sizeof(dContact));

		if( iNumContacts > 0 )
		{	
			CDynamicsObject* dObject1 = NULL;
			CDynamicsObject* dObject2 = NULL;

			bool hasSet = false;

			if(b1)
			{
				dObject1 = (CDynamicsObject*)dBodyGetData(b1);
				if(dObject1)
				{
					CDynamicsObject::ContactAction eAction = dObject1->CheckContact(contact, iNumContacts, b1, o1, b2, o2);
					if(eAction == CDynamicsObject::CA_DontCollide)
					{
						// no collision
						return;
					}
					hasSet = eAction == CDynamicsObject::CA_ConfiguredContacts;
				}
			}

			if(b2 && !hasSet)
			{
				dObject2 = (CDynamicsObject*)dBodyGetData(b2);
				if(dObject2)
				{
					CDynamicsObject::ContactAction eAction = dObject2->CheckContact(contact, iNumContacts, b2, o2, b1, o1);
					if(eAction == CDynamicsObject::CA_DontCollide)
					{
						// no collision
						return;
					}
					hasSet = eAction == CDynamicsObject::CA_ConfiguredContacts;
				}
			}

			if(!hasSet)
			{
				for (int i=0; i<iNumContacts; i++) 
				{
	/*				contact[i].surface.mode = dContactBounce | dContactSoftCFM;
					contact[i].surface.mu = 250;
					contact[i].surface.mu2 = 0;					// definiert Reibung in zweite Richtung; wird nicht genutzt, wenn dContactMu2-Flag nicht gesetzt ist
					contact[i].surface.bounce = 0.1;
					contact[i].surface.bounce_vel = 0.1;
					contact[i].surface.soft_cfm = 0.01;
	/*/
					contact[i].surface.mode = dContactSlip1 | dContactSlip2 | dContactApprox1
											| dContactBounce | dContactSoftERP | dContactSoftCFM;

					contact[i].surface.mu = dInfinity;
					contact[i].surface.slip1 = 0.0001;
					contact[i].surface.slip2 = 0.0001;
					contact[i].surface.soft_erp = 0.8;
					contact[i].surface.soft_cfm = 0.00001;
					contact[i].surface.bounce = 0.0;
					contact[i].surface.bounce_vel = 0.0;
				}
			}

			for (int i=0; i<iNumContacts; ++i) 
			{
				dJointID c = dJointCreateContact(*pxThis->m_pWorld, *pxThis->m_pContactJointGroup, contact+i);
				dJointAttach (c, b1, b2);
			}
		}
	}
}
//---------------------------------------------------------------------------------------------------------------------
