package net.mohron.skyclaims.world.region;

import me.ryanhamshire.griefprevention.api.claim.ClaimResult;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.util.ClaimUtil;
import net.mohron.skyclaims.util.ConfigUtil;
import net.mohron.skyclaims.world.Island;

import java.util.ArrayList;

public class SpiralRegionPattern implements IRegionPattern {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static final int SPAWN_REGIONS = ConfigUtil.getSpawnRegions();

	/**
	 * A method to generate a region-scaled spiral region and return the x/y pairs of each region
	 *
	 * @return An ArrayList of Points containing the x,y of regions, representing a spiral shape
	 */
	public ArrayList<Region> generateRegionPattern() {
		int islandCount = SkyClaims.islands.size();
		int generationSize = (int) Math.sqrt((double) islandCount + SPAWN_REGIONS) + 1;
		String log = "Region Pattern: [";

		ArrayList<Region> coordinates = new ArrayList<>(generationSize);
		int[] delta = {0, -1};
		int x = 0;
		int y = 0;

		for (int i = (int) Math.pow(Math.max(generationSize, generationSize), 2); i > 0; i--) {
			if (x == y || (x < 0 && x == -y) || (x > 0 && x == 1 - y)) {
				// change direction
				int a = delta[0];
				delta[0] = -delta[1];
				delta[1] = a;
			}
			coordinates.add(new Region(x, y));
			log += String.format("(%s,%s),", x, y);
			x += delta[0];
			y += delta[1];
		}
		PLUGIN.getLogger().debug(log + "]");
		PLUGIN.getLogger().debug(String.format("Coordinates length: %s", coordinates.size()));
		return coordinates;
	}

	public Region nextRegion() {
		ArrayList<Region> spawnRegions = new ArrayList<>(SPAWN_REGIONS);
		ArrayList<Island> currentIslands = new ArrayList<>(SkyClaims.islands.values());
		ArrayList<Region> regions = generateRegionPattern();
		int iterator = 0;

		PLUGIN.getLogger().debug(String.format("Checking for next region out of %s points with %s spawn regions.", regions.size(), SPAWN_REGIONS));

		for (Region region : regions) {
			if (iterator < SPAWN_REGIONS) {
				spawnRegions.add(region);
				PLUGIN.getLogger().debug(String.format("Skipping (%s, %s) for spawn", region.getX(), region.getZ()));
				iterator++;
				continue;
			} else {
				if (SkyClaims.islands.size() > 1) {
					ClaimResult claimResult = ClaimUtil.createSpawnClaim(spawnRegions);
					if (claimResult.successful()) {
						PLUGIN.getLogger().info(String.format("Reserved %s regions for spawn. Admin Claim: %s", SPAWN_REGIONS, claimResult.getClaim().get().getUniqueId()));
					}
				}
			}


			PLUGIN.getLogger().debug(String.format("Checking region (%s, %s) for world", region.getX(), region.getZ()));

			if (Region.isOccupied(region)) {
				iterator++;
				continue;
			} else {
				return region;
			}
		}

		return regions.get(regions.size() - 1);
	}
}