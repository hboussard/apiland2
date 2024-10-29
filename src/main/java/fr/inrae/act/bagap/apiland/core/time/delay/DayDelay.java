/*Copyright 2010 by INRA SAD-Paysage (Rennes)

Author: Hugues BOUSSARD 
email : hugues.boussard@rennes.inra.fr

This library is a JAVA toolbox made to create and manage dynamic landscapes.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use,
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info".

As a counterpart to the access to the source code and rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability.

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate, and that also
therefore means  that it is reserved for developers and experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or
data to be ensured and,  more generally, to use and operate it in the
same conditions as regards security.

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
*/
package fr.inrae.act.bagap.apiland.core.time.delay;

import fr.inrae.act.bagap.apiland.core.time.Instant;
import fr.inrae.act.bagap.apiland.core.time.Interval;

public class DayDelay extends Delay{

	/**
	 * version number
	 */
	private static final long serialVersionUID = 1L;
	
	private final static long msByDay = 86400000;
	
	public DayDelay(long delay){
		this.delay = delay;
	}
	
	@Override
	public Instant next(Instant t){
		return new Instant(t.get()+(delay*msByDay));
	}

	@Override
	public Instant previous(Instant t) {
		return new Instant(t.get()-(delay*msByDay));
	}
	
	@Override
	public Interval next(Interval t){
		return new Interval(new Instant(t.start().get()+(delay*msByDay)),new Instant(t.end().get()+(delay*msByDay)));
	}

	@Override
	public Interval previous(Interval t) {
		return new Interval(new Instant(t.start().get()-(delay*msByDay)),new Instant(t.end().get()-(delay*msByDay)));
	}

	@Override
	public long get() {
		return delay;
	}
}
