using System;
using System.Text.RegularExpressions;

namespace DadoTramposo
{
    static class Utils
    {
        private static bool IsValidPositiveInt(this string value)
        {
            return Regex.IsMatch(value, @"^\d+$");
        }

        private static int ToPositiveInt(this string value)
        {
            if (value.IsValidPositiveInt())
            {
                return Convert.ToInt32(value);
            }

            throw new FormatException("El valor no es un entero positivo válido.");
        }

   
       
      
        public static int ReadIntInput(string fieldName)
        {
            Console.Write($"Digite el valor del campo {fieldName}: ");
            var input = Console.ReadLine();

            while (!input.IsValidPositiveInt())
            {
                Console.Write($"Valor inválido. Digite el valor del campo {fieldName}nuevamente: ");
                input = Console.ReadLine();
            }

            return input.ToPositiveInt();
        }
    }
}
