// CCS811 Air Quality Sensor demo program
// Copyright (c) 2017 Larry Bank
// email: bitbank@pobox.com
// Project started 11/5/2017
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <unistd.h>

#include <ccs811.h>

int main(int argc, char *argv[])
{
	int i;
	int eCO2, TVOC;

	i = ccs811Init(1, 0x5A);
	if (i != 0)
	{
		return -1;
	}
	usleep(1000000);
	while(1)
	{
		if (ccs811ReadValues(&eCO2, &TVOC))
		{
			printf("%d/%d\n", eCO2, TVOC);
			fflush(stdout);
		}
		usleep(5000000);
	}

	return 0;
}
