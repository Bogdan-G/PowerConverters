package powercrystals.powerconverters.power.systems.fortron;

import net.minecraftforge.common.util.ForgeDirection;
import powercrystals.powerconverters.power.PowerSystemManager;
import powercrystals.powerconverters.power.base.TileEntityEnergyConsumer;
import powercrystals.powerconverters.power.systems.PowerFortron;

import resonant.api.mffs.fortron.IFortronStorage;

import java.util.Map.Entry;
import java.lang.Math;

public class TileEntityFortronConsumer extends TileEntityEnergyConsumer<IFortronStorage> {

    private int lastReceivedFortron;

    public TileEntityFortronConsumer() {
        super(PowerSystemManager.getInstance().getPowerSystemByName(PowerFortron.id), 0, IFortronStorage.class);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if(worldObj.isRemote) {
        	return;
        }
        boolean powered = getWorldObj().getStrongestIndirectPower(xCoord, yCoord, zCoord) > 0;
        if(powered) {
            return;
        }

        //
        double perInput = getPowerSystem().getInternalEnergyPerInput(0);
		int demand = getTotalEnergyDemand();
		
		if( (demand / perInput) < 1.0 ) {
			return; // we're not going to waste fortron energy we can't store.
		}
		
		// Demand internal units / perinput
		int totalFortronDemand = (int)Math.floor(demand / perInput);
		int fortronReceived;
		
		for (Entry<ForgeDirection, IFortronStorage> it : this.getTiles().entrySet()) {		
			IFortronStorage fs = it.getValue();
			
			fortronReceived = fs.requestFortron(totalFortronDemand, true);
					
			storeEnergy( (fortronReceived * perInput), false);
			
			totalFortronDemand -= fortronReceived;
			
			// 
			lastReceivedFortron += fortronReceived;
						
			if(totalFortronDemand <= 0)
				break;
		}
		
				        
  
    }

    @Override
    public double getInputRate() {
        int last = lastReceivedFortron;
        lastReceivedFortron = 0;
        return last;
    }

}
