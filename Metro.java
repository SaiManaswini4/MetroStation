package com.srkr.project;

import java.util.Scanner;
import java.util.LinkedList;
import java.util.ArrayList;

public class Metro {

	class Vertex {
		HashTable<String, Integer> nbrs = new HashTable<>();
	}

	static HashTable<String, Vertex> vtces;

	public Metro() {
		vtces = new HashTable<>();
	}

	public boolean containsVertex(String vname) {
		return vtces.containsKey(vname);
	}

	public void addVertex(String vname) {
		Vertex vtx = new Vertex();
		vtces.put(vname, vtx);
	}

	public void removeVertex(String vname) {
		Vertex vtx = vtces.get(vname);
		ArrayList<String> keys = new ArrayList<>(vtx.nbrs.keySet());

		for (String key : keys) {
			Vertex nbrVtx = vtces.get(key);
			nbrVtx.nbrs.remove(vname);
		}

		vtces.remove(vname);
	}

	public int numEdges() {
		ArrayList<String> keys = new ArrayList<>(vtces.keySet());
		int count = 0;

		for (String key : keys) {
			Vertex vtx = vtces.get(key);
			count = count + vtx.nbrs.size();
		}

		return count / 2;
	}

	public boolean containsEdge(String vname1, String vname2) {
		Vertex vt1 = vtces.get(vname1);
		Vertex vt2 = vtces.get(vname2);

		if (vt1 == null || vt2 == null || !vt1.nbrs.containsKey(vname2)) {
			return false;
		}

		return true;

	}

	public void addEdge(String vname1, String vname2, int value) {
		Vertex vtx1 = vtces.get(vname1);
		Vertex vtx2 = vtces.get(vname2);

		if (vtx1 == null || vtx2 == null || vtx1.nbrs.containsKey(vname2)) {
			return;
		}

		vtx1.nbrs.put(vname2, value);
		vtx2.nbrs.put(vname1, value);
	}

	public void removeEdge(String vname1, String vname2) {
		Vertex vtx1 = vtces.get(vname1);
		Vertex vtx2 = vtces.get(vname2);

		if (vtx1 == null || vtx2 == null || !vtx1.nbrs.containsKey(vname2)) {
			return;
		}

		vtx1.nbrs.remove(vname2);
		vtx2.nbrs.remove(vname1);
	}

	public void displayMap() {
		System.out.println("\t Hyderabad Metro Map");
		System.out.println("\t----------------------");
		System.out.println("--------------------------------------------------------------\n");
		ArrayList<String> keys = new ArrayList<>(vtces.keySet());

		for (String key : keys) {
			String str = key + "=>\n";
			Vertex vtx = vtces.get(key);
			ArrayList<String> vtxnbrs = new ArrayList<>(vtx.nbrs.keySet());

			for (String nbr : vtxnbrs) {
				str = str + "\t" + nbr + "\t";
				if (nbr.length() < 16)
					str = str + "\t";
				if (nbr.length() < 8)
					str = str + "\t";
				str = str + vtx.nbrs.get(nbr) + "\n";
			}
			System.out.println(str);
		}
		System.out.println("\t------------------");
		System.out.println("---------------------------------------------------\n");

	}

	public void displayStations() {
		System.out.println("\n***********************************************************************\n");
		ArrayList<String> keys = new ArrayList<>(vtces.keySet());
		int i = 1;
		for (String key : keys) {
			System.out.println(i + ". " + key);
			i++;
		}
		System.out.println("\n***********************************************************************\n");
	}

	public boolean hasPath(String vname1, String vname2, HashTable<String, Boolean> processed) {
		if (containsEdge(vname1, vname2)) {
			return true;
		}

		processed.put(vname1, true);

		Vertex vtx = vtces.get(vname1);
		ArrayList<String> nbrs = new ArrayList<>(vtx.nbrs.keySet());

		for (String nbr : nbrs) {
			if (!processed.containsKey(nbr))
				if (hasPath(nbr, vname2, processed))
					return true;
		}

		return false;
	}

	private class DijkstraPair implements Comparable<DijkstraPair> {
		String vname;
		String psf;
		int cost;

		@Override
		public int compareTo(DijkstraPair o) {
			return o.cost - this.cost;
		}
	}

	public int dijkstra(String src, String des, boolean nan) {
		int val = 0;
		ArrayList<String> ans = new ArrayList<>();
		HashTable<String, DijkstraPair> map = new HashTable<>();

		Heap<DijkstraPair> heap = new Heap<>();

		for (String key : vtces.keySet()) {
			DijkstraPair np = new DijkstraPair();
			np.vname = key;
			np.cost = Integer.MAX_VALUE;

			if (key.equals(src)) {
				np.cost = 0;
				np.psf = key;
			}

			heap.add(np);
			map.put(key, np);
		}

		while (!heap.isEmpty()) {
			DijkstraPair rp = heap.remove();

			if (rp.vname.equals(des)) {
				val = rp.cost;
				break;
			}

			map.remove(rp.vname);

			ans.add(rp.vname);

			Vertex v = vtces.get(rp.vname);
			for (String nbr : v.nbrs.keySet()) {
				if (map.containsKey(nbr)) {
					int oc = map.get(nbr).cost;
					Vertex k = vtces.get(rp.vname);
					int nc;
					if (nan)
						nc = rp.cost + 120 + 40 * k.nbrs.get(nbr);
					else
						nc = rp.cost + k.nbrs.get(nbr);

					if (nc < oc) {
						DijkstraPair gp = map.get(nbr);
						gp.psf = rp.psf + nbr;
						gp.cost = nc;

						heap.updatePriority(gp);
					}
				}
			}
		}
		return val;
	}

	private class Pair {
		String vname;
		String psf;
		int min_dis;
		int min_time;
	}

	public String GetMinimumDistance(String src, String dst) {
		int min = Integer.MAX_VALUE;
		String ans = "";
		HashTable<String, Boolean> processed = new HashTable<>();
		LinkedList<Pair> stack = new LinkedList<>();

		Pair sp = new Pair();
		sp.vname = src;
		sp.psf = src + "  ";
		sp.min_dis = 0;
		sp.min_time = 0;

		stack.addFirst(sp);

		while (!stack.isEmpty()) {
			Pair rp = stack.removeFirst();

			if (processed.containsKey(rp.vname))
				continue;
			processed.put(rp.vname, true);

			if (rp.vname.equals(dst)) {
				int temp = rp.min_dis;
				if (temp < min) {
					ans = rp.psf;
					min = temp;
				}
				continue;
			}

			Vertex rpvtx = vtces.get(rp.vname);
			ArrayList<String> nbrs = new ArrayList<>(rpvtx.nbrs.keySet());

			for (String nbr : nbrs) {
				if (!processed.containsKey(nbr)) {

					Pair np = new Pair();
					np.vname = nbr;
					np.psf = rp.psf + nbr + "  ";
					np.min_dis = rp.min_dis + rpvtx.nbrs.get(nbr);
					stack.addFirst(np);
				}
			}
		}
		ans = ans + Integer.toString(min);
		return ans;
	}

	public String GetMinimumTime(String src, String dst) {
		int min = Integer.MAX_VALUE;
		String ans = "";
		HashTable<String, Boolean> processed = new HashTable<>();
		LinkedList<Pair> stack = new LinkedList<>();

		Pair sp = new Pair();
		sp.vname = src;
		sp.psf = src + "  ";
		sp.min_dis = 0;
		sp.min_time = 0;

		stack.addFirst(sp);

		while (!stack.isEmpty()) {
			Pair rp = stack.removeFirst();
			if (processed.containsKey(rp.vname))
				continue;

			processed.put(rp.vname, true);

			if (rp.vname.equals(dst)) {
				int temp = rp.min_time;
				if (temp < min) {
					ans = rp.psf;
					min = temp;
				}
				continue;
			}

			Vertex rpvtx = vtces.get(rp.vname);
			ArrayList<String> nbrs = new ArrayList<>(rpvtx.nbrs.keySet());

			for (String nbr : nbrs) {
				if (!processed.containsKey(nbr)) {
					Pair np = new Pair();
					np.vname = nbr;
					np.psf = rp.psf + nbr + "  ";
					np.min_time = rp.min_time + 120 + 40 * rpvtx.nbrs.get(nbr);
					stack.addFirst(np);
				}
			}
		}
		Double minutes = Math.ceil((double) min / 60);
		ans = ans + Double.toString(minutes);
		return ans;
	}

	public ArrayList<String> getInterchanges(String str) {
		ArrayList<String> arr = new ArrayList<>();
		String res[] = str.split("  ");
		arr.add(res[0]);
		int count = 0;
		for (int i = 1; i < res.length - 1; i++) {
			int index = res[i].indexOf('~');
			String s = res[i].substring(index + 1);

			if (s.length() == 2) {
				String prev = res[i - 1].substring(res[i - 1].indexOf('~') + 1);
				String next = res[i + 1].substring(res[i + 1].indexOf('~') + 1);

				if (prev.equals(next))
					arr.add(res[i]);
				else {
					arr.add(res[i] + " ==> " + res[i + 1]);
					i++;
					count++;
				}
			} else
				arr.add(res[i]);
		}
		arr.add(Integer.toString(count));
		arr.add(res[res.length - 1]);
		return arr;
	}

	public static void Create_Metro_Map(Metro m) {
		m.addVertex("Miyapur~Y");
		m.addVertex("JNTU College~Y");
		m.addVertex("Kukatpally~Y");
		m.addVertex("Moosapet~YO");
		m.addVertex("Bharat Nagar~Y");
		m.addVertex("Ameerpet~YO");
		m.addVertex("Punjagutta~Y");
		m.addVertex("Khairatabad~YO");
		m.addVertex("Lakdi-ka-pul~YO");
		m.addVertex("Assembly~YO");
		m.addVertex("Nampally~YO");
		m.addVertex("Gandhi Bhavan~YO");
		m.addVertex("MG Bus Station~Y");
		m.addVertex("Malakpet~YO");
		m.addVertex("DilsukhNagar~YO");
		m.addVertex("LB Nagar~Y");
		m.addVertex("Raidurg~Y");
		m.addVertex("HiTech City~Y");
		m.addVertex("Durgam Cheruvu~Y");
		m.addVertex("Madhapur~Y");
		m.addVertex("Jubliee hills CheckPost~Y");
		m.addVertex("Yusufguda~Y");
		m.addVertex("Begumpet~Y");
		m.addVertex("Paradise~YO");
		m.addVertex("Secunderabad East~Y");
		m.addVertex("Tarnaka~Y");
		m.addVertex("Uppal~Y");
		m.addVertex("Nagole~YO");
		m.addVertex("JBS Parade Ground~YO");
		m.addVertex("Secunderabad West~Y");
		m.addVertex("Chikkadpally~Y");

		m.addEdge("Miyapur~Y", "JNTU College~Y", 2);
		m.addEdge("JNTU College~Y", "Kukatpally~Y", 2);
		m.addEdge("Kukatpally~Y", "Moosapet~YO", 4);
		m.addEdge("Moosapet~YO", "Bharat Nagar~Y", 1);
		m.addEdge("Bharat Nagar~Y", "Ameerpet~YO", 5);
		m.addEdge("Ameerpet~YO", "Punjagutta~Y", 2);
		m.addEdge("Punjagutta~Y", "Khairatabad~YO", 2);
		m.addEdge("Khairatabad~YO", "Lakdi-ka-pul~YO", 1);
		m.addEdge("Lakdi-ka-pul~YO", "Assembly~YO", 1);
		m.addEdge("Assembly~YO", "Nampally~YO", 1);
		m.addEdge("Nampally~YO", "Gandhi Bhavan~YO", 2);
		m.addEdge("Gandhi Bhavan~YO", "MG Bus Station~Y", 2);
		m.addEdge("MG Bus Station~Y", "Malakpet~YO", 1);
		m.addEdge("Malakpet~YO", "DilsukhNagar~YO", 3);
		m.addEdge("DilsukhNagar~YO", "LB Nagar~Y", 3);
		m.addEdge("Raidurg~Y", "HiTech City~Y", 5);
		m.addEdge("HiTech City~Y", "Durgam Cheruvu~Y", 2);
		m.addEdge("Durgam Cheruvu~Y", "Madhapur~Y", 6);
		m.addEdge("Madhapur~Y", "Jubliee hills CheckPost~Y", 5);
		m.addEdge("Jubliee hills CheckPost~Y", "Yusufguda~Y", 6);
		m.addEdge("Yusufguda~Y", "Ameerpet~YO", 4);
		m.addEdge("Ameerpet~YO", "Begumpet~Y", 2);
		m.addEdge("Begumpet~Y", "Paradise~YO", 2);
		m.addEdge("Paradise~YO", "JBS Parade Ground~YO", 2);
		m.addEdge("JBS Parade Ground~YO", "Secunderabad East~Y", 1);
		m.addEdge("Secunderabad East~Y", "Tarnaka~Y", 5);
		m.addEdge("Tarnaka~Y", "Uppal~Y", 7);
		m.addEdge("Uppal~Y", "Nagole~YO", 4);
		m.addEdge("JBS Parade Ground~YO", "Secunderabad West~Y", 1);
		m.addEdge("Secunderabad West~Y", "Chikkadpally~Y", 5);
		m.addEdge("Chikkadpally~Y", "MG Bus Station~Y", 3);
		;

	}

	public static void main(String[] args) {
		Metro m = new Metro();
		Create_Metro_Map(m);
		Scanner sc = new Scanner(System.in);
		System.out.println("\n\t\t\t****WELCOME TO THE METRO APP*****");
		while (true) {
			System.out.println("\t\t\t\t~~LIST OF ACTIONS~~\n\n");
			System.out.println("1. LIST ALL THE STATIONS IN THE MAP");
			System.out.println("2. SHOW THE METRO MAP");
			System.out.println("3. GET SHORTEST DISTANCE FROM A 'SOURCE' STATION TO 'DESTINATION' STATION");
			System.out.println("4. GET SHORTEST TIME TO REACH FROM A 'SOURCE' STATION TO 'DESTINATION' STATION");
			System.out.println("5. GET SHORTEST PATH (DISTANCE WISE) TO REACH FROM A 'SOURCE' STATION TO 'DESTINATION' STATION");
			System.out.println("6. GET SHORTEST PATH (TIME WISE) TO REACH FROM A 'SOURCE' STATION TO 'DESTINATION' STATION");
			System.out.println("7. EXIT THE MENU");
			System.out.print("\nENTER YOUR CHOICE FROM THE ABOVE LIST (1 to 7) : ");
			int choice = sc.nextInt();
			System.out.print("\n***********************************************************\n");
			switch (choice) {
			case 1:
				m.displayStations();
				break;
			case 2:
				m.displayMap();
				break;
			case 3:
				System.out.println("ENTER THE SOURCE STATION : ");
				sc.nextLine();
				String st1=sc.nextLine();
				System.out.println("ENTER THE DESTINATION STATION : ");
				String st2 = sc.nextLine();
				System.out.println(st1 + "\t" + st2);
				HashTable<String, Boolean> processed = new HashTable<>();
				if (!m.containsVertex(st1) || !m.containsVertex(st2) || !m.hasPath(st1, st2, processed))
					System.out.println("THE INPUTS ARE INVALID");
				else
					System.out.println("SHORTEST DISTANCE FROM " + st1 + " TO " + st2 + " IS "
							+ m.dijkstra(st1, st2, false) + "KM\n");
				break;
			case 4:
				System.out.print("ENTER THE SOURCE STATION: ");
				sc.nextLine();
				String sat1 = sc.nextLine();
				System.out.print("ENTER THE DESTINATION STATION: ");
				String sat2 = sc.nextLine();
				System.out.println(sat1 + "\t" + sat2);
				HashTable<String, Boolean> processed1 = new HashTable<>();
				if (!m.containsVertex(sat1) || !m.containsVertex(sat2) || !m.hasPath(sat1, sat2, processed1))
					System.out.println("THE INPUTS ARE INVALID");
				else
					System.out.println("SHORTEST TIME FROM (" + sat1 + ") TO (" + sat2 + ") IS "
							+ m.dijkstra(sat1, sat2, true) / 60 + " MINUTES\n\n");
				break;
			case 5:
				System.out.println("ENTER THE SOURCE STATION:");
				sc.nextLine();
				String s1 = sc.nextLine();
				System.out.println("ENTER THE DESTINATION STATION:");
				String s2 = sc.nextLine();
				System.out.println(s1 + "\t" + s2);

				HashTable<String, Boolean> processed2 = new HashTable<>();
				if (!m.containsVertex(s1) || !m.containsVertex(s2) || !m.hasPath(s1, s2, processed2))
					System.out.println("THE INPUTS ARE INVALID");
				else {
					ArrayList<String> str = m.getInterchanges(m.GetMinimumDistance(s1, s2));
					int len = str.size();
					System.out.println("SOURCE STATION : " + s1);
					System.out.println("DESTINATION STATION : " + s2);
					System.out.println("DISTANCE : " + str.get(len - 1));
					System.out.println("NUMBER OF INTERCHANGES : " + str.get(len - 2));
					System.out.println("~~~~~~~~~~~~~");
					System.out.println("START  ==>  " + str.get(0));
					for (int i = 1; i < len - 3; i++) {
						System.out.println(str.get(i));
					}
					System.out.print(str.get(len - 3) + "   ==>    END");
					System.out.println("\n~~~~~~~~~~~~~");
				}
				break;
			case 6:
				System.out.print("ENTER THE SOURCE STATION: ");
				sc.nextLine();
				String ss1 = sc.nextLine();
				System.out.print("ENTER THE DESTINATION STATION: ");
				String ss2 = sc.nextLine();
				System.out.println(ss1 + "\t" + ss2);
				HashTable<String, Boolean> processed3 = new HashTable<>();
				if (!m.containsVertex(ss1) || !m.containsVertex(ss2) || !m.hasPath(ss1, ss2, processed3))
					System.out.println("THE INPUTS ARE INVALID");
				else {
					ArrayList<String> str = m.getInterchanges(m.GetMinimumTime(ss1, ss2));
					int len = str.size();
					System.out.println("SOURCE STATION : " + ss1);
					System.out.println("DESTINATION STATION : " + ss2);
					System.out.println("TIME : " + str.get(len - 1) + " MINUTES");
					System.out.println("NUMBER OF INTERCHANGES : " + str.get(len - 2));
					System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
					System.out.print("START  ==>  " + str.get(0) + " ==>  ");
					for (int i = 1; i < len - 3; i++) {
						System.out.println(str.get(i));
					}
					System.out.print(str.get(len - 3) + "   ==>    END");
					System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
				}
				break;
			case 7:
				System.out.println("THANKS FOR CHOOSING OUR APPLICATION ^_^");
				System.exit(0);

			default:
				System.out.println("Please enter a valid option! ");
				System.out.println("The options you can choose are from 1 to 6. ");

			}

		}

	}
}